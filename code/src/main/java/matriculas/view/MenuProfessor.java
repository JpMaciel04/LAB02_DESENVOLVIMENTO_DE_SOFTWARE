package matriculas.view;

import java.util.List;
import matriculas.controller.ProfessorController;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;
import matriculas.model.Professor;
import matriculas.service.RegraNegocioException;

/** Casos de uso do professor: UC11. */
public class MenuProfessor {

    private final Console console;
    private final ProfessorController controller;

    public MenuProfessor(Console console, ProfessorController controller) {
        this.console = console;
        this.controller = controller;
    }

    public void executar(Professor professor) {
        while (true) {
            console.titulo("PROFESSOR - " + professor.getNome());
            int opcao = console.menu("Minhas disciplinas", "Consultar alunos matriculados", "Sair");
            try {
                switch (opcao) {
                    case 1 -> listarDisciplinas(professor);
                    case 2 -> consultarAlunos(professor);
                    default -> {
                        return;
                    }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void listarDisciplinas(Professor professor) {
        List<Disciplina> disciplinas = controller.listarMinhasDisciplinas(professor);
        if (disciplinas.isEmpty()) {
            console.linha("Você não leciona nenhuma disciplina.");
            return;
        }
        console.linha("%-8s %-32s %-12s %s", "CÓDIGO", "DISCIPLINA", "TIPO", "SITUAÇÃO");
        for (Disciplina d : disciplinas) {
            console.linha("%-8s %-32s %-12s %s", d.getCodigo(), d.getNome(), d.getTipo(), d.getSituacao());
        }
    }

    private void consultarAlunos(Professor professor) {
        listarDisciplinas(professor);
        String codigo = console.lerTexto("Código da disciplina");
        List<Aluno> alunos = controller.consultarAlunosMatriculados(professor, codigo);
        console.linha("");
        if (alunos.isEmpty()) {
            console.linha("Nenhum aluno matriculado.");
        }
        for (Aluno aluno : alunos) {
            console.linha("  %-10s %s", aluno.getId(), aluno.getNome());
        }
        int minimo = Disciplina.getMinimoAlunosAtivacao();
        console.linha("Total: %d aluno(s) - %s", alunos.size(), alunos.size() >= minimo
                ? "mínimo de " + minimo + " alunos atingido"
                : "abaixo do mínimo de " + minimo + " alunos para ativação");
    }
}
