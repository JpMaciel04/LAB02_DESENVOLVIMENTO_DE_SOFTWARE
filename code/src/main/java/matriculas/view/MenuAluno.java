package matriculas.view;

import java.util.List;
import matriculas.controller.AlunoController;
import matriculas.model.Aluno;
import matriculas.model.Curriculo;
import matriculas.model.Disciplina;
import matriculas.model.Matricula;
import matriculas.model.PeriodoMatricula;
import matriculas.service.RegraNegocioException;

/** Casos de uso do aluno: UC08, UC09, UC10 e US11. */
public class MenuAluno {

    private final Console console;
    private final AlunoController controller;

    public MenuAluno(Console console, AlunoController controller) {
        this.console = console;
        this.controller = controller;
    }

    public void executar(Aluno aluno) {
        while (true) {
            console.titulo("ALUNO - " + aluno.getNome() + " (" + aluno.getId() + ")");
            exibirSituacaoPeriodo();
            int opcao = console.menu("Consultar disciplinas disponíveis", "Matricular-se em disciplina",
                    "Cancelar matrícula", "Minhas matrículas", "Confirmar inscrição do semestre (cobrança)",
                    "Sair");
            try {
                switch (opcao) {
                    case 1 -> listarDisponiveis();
                    case 2 -> matricular(aluno);
                    case 3 -> cancelar(aluno);
                    case 4 -> listarMinhasMatriculas(aluno);
                    case 5 -> concluirInscricao(aluno);
                    default -> {
                        return;
                    }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void exibirSituacaoPeriodo() {
        try {
            Curriculo curriculo = controller.obterCurriculoVigente();
            PeriodoMatricula periodo = curriculo.getPeriodoMatricula();
            String datas = periodo == null ? "não definido"
                    : Console.formatar(periodo.getDataInicio()) + " a " + Console.formatar(periodo.getDataFim());
            console.linha("Semestre %s | Período de matrículas: %s | %s", curriculo.getSemestre(), datas,
                    controller.periodoAberto() ? "ABERTO" : "FECHADO");
        } catch (RegraNegocioException e) {
            console.linha(e.getMessage());
        }
    }

    private void listarDisponiveis() {
        List<Disciplina> disciplinas = controller.consultarDisciplinasDisponiveis();
        if (disciplinas.isEmpty()) {
            console.linha("Nenhuma disciplina disponível no currículo vigente.");
            return;
        }
        console.linha("%-8s %-32s %-12s %-22s %s", "CÓDIGO", "DISCIPLINA", "TIPO", "PROFESSOR", "VAGAS");
        for (Disciplina d : disciplinas) {
            int vagas = controller.vagasRestantes(d);
            console.linha("%-8s %-32s %-12s %-22s %s", d.getCodigo(), d.getNome(), d.getTipo(),
                    d.getProfessor().getNome(), vagas > 0 ? String.valueOf(vagas) : "SEM VAGAS");
        }
    }

    private void matricular(Aluno aluno) {
        listarDisponiveis();
        Matricula matricula = controller.matricular(aluno, console.lerTexto("Código da disciplina"));
        console.sucesso("Matriculado em " + matricula.getDisciplina().getNome() + ".");
        console.linha("Lembre-se de confirmar a inscrição do semestre (opção 5) ao terminar.");
    }

    private void cancelar(Aluno aluno) {
        listarMinhasMatriculas(aluno);
        String codigo = console.lerTexto("Código da disciplina a cancelar");
        if (console.confirmar("Confirma o cancelamento?")) {
            controller.cancelarMatricula(aluno, codigo);
            console.sucesso("Matrícula cancelada; a vaga foi liberada.");
        }
    }

    private void listarMinhasMatriculas(Aluno aluno) {
        List<Matricula> matriculas = controller.consultarMinhasMatriculas(aluno);
        if (matriculas.isEmpty()) {
            console.linha("Você não possui matrículas no semestre vigente.");
            return;
        }
        console.linha("%-8s %-32s %-12s %-22s %-10s %-10s %s", "CÓDIGO", "DISCIPLINA", "TIPO", "PROFESSOR",
                "MATRÍCULA", "DISCIPL.", "DATA");
        for (Matricula m : matriculas) {
            Disciplina d = m.getDisciplina();
            console.linha("%-8s %-32s %-12s %-22s %-10s %-10s %s", d.getCodigo(), d.getNome(), d.getTipo(),
                    d.getProfessor().getNome(), m.getSituacao(), d.getSituacao(),
                    Console.formatar(m.getDataMatricula()));
        }
        console.linha("Inscrição do semestre: %s",
                controller.inscricaoConcluida(aluno) ? "CONFIRMADA" : "pendente de confirmação");
    }

    private void concluirInscricao(Aluno aluno) {
        if (controller.concluirInscricao(aluno)) {
            console.sucesso("Inscrição confirmada. O sistema de cobranças foi notificado.");
        } else {
            console.erro("Inscrição confirmada, mas o sistema de cobranças não pôde ser notificado agora.");
        }
    }
}
