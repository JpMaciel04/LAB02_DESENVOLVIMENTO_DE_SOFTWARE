package matriculas.view;

import java.util.List;
import java.util.stream.Collectors;
import matriculas.controller.SecretariaController;
import matriculas.model.Aluno;
import matriculas.model.Curriculo;
import matriculas.model.Curso;
import matriculas.model.Disciplina;
import matriculas.model.PeriodoMatricula;
import matriculas.model.Professor;
import matriculas.model.Secretaria;
import matriculas.model.TipoDisciplina;
import matriculas.model.Usuario;
import matriculas.service.RegraNegocioException;

/** Casos de uso da secretaria: UC02 a UC07 e UC12. */
public class MenuSecretaria {

    private final Console console;
    private final SecretariaController controller;

    public MenuSecretaria(Console console, SecretariaController controller) {
        this.console = console;
        this.controller = controller;
    }

    public void executar(Secretaria secretaria) {
        while (true) {
            console.titulo("SECRETARIA - " + secretaria.getNome());
            int opcao = console.menu("Cursos", "Disciplinas", "Professores", "Alunos", "Currículo do semestre",
                    "Período de matrículas", "Sair");
            switch (opcao) {
                case 1 -> menuCursos();
                case 2 -> menuDisciplinas();
                case 3 -> menuProfessores();
                case 4 -> menuAlunos();
                case 5 -> menuCurriculo();
                case 6 -> menuPeriodo();
                default -> {
                    return;
                }
            }
        }
    }

    // ------------------------------------------------------------------ UC02

    private void menuCursos() {
        while (true) {
            console.titulo("CURSOS");
            int opcao = console.menu("Listar", "Cadastrar", "Alterar", "Remover", "Voltar");
            if (opcao == 0) {
                return;
            }
            try {
                switch (opcao) {
                    case 1 -> listarCursos();
                    case 2 -> {
                        Curso curso = controller.cadastrarCurso(console.lerTexto("Código"), console.lerTexto("Nome"),
                                console.lerInteiro("Número de créditos"));
                        console.sucesso("Curso " + curso.getNome() + " cadastrado.");
                    }
                    case 3 -> {
                        listarCursos();
                        Curso curso = controller.buscarCurso(console.lerTexto("Código do curso"));
                        controller.alterarCurso(curso.getCodigo(), console.lerTexto("Nome", curso.getNome()),
                                console.lerInteiro("Número de créditos", curso.getNumeroCreditos()));
                        console.sucesso("Curso alterado.");
                    }
                    case 4 -> {
                        listarCursos();
                        controller.removerCurso(console.lerTexto("Código do curso"));
                        console.sucesso("Curso removido.");
                    }
                    default -> { }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void listarCursos() {
        List<Curso> cursos = controller.listarCursos();
        if (cursos.isEmpty()) {
            console.linha("Nenhum curso cadastrado.");
            return;
        }
        console.linha("%-8s %-36s %-8s %s", "CÓDIGO", "NOME", "CRÉDITOS", "DISCIPLINAS");
        for (Curso c : cursos) {
            console.linha("%-8s %-36s %-8d %d", c.getCodigo(), c.getNome(), c.getNumeroCreditos(),
                    c.getDisciplinas().size());
        }
    }

    // ------------------------------------------------------------------ UC03

    private void menuDisciplinas() {
        while (true) {
            console.titulo("DISCIPLINAS");
            int opcao = console.menu("Listar", "Cadastrar", "Alterar", "Remover", "Voltar");
            if (opcao == 0) {
                return;
            }
            try {
                switch (opcao) {
                    case 1 -> listarDisciplinas();
                    case 2 -> cadastrarDisciplina();
                    case 3 -> alterarDisciplina();
                    case 4 -> {
                        listarDisciplinas();
                        controller.removerDisciplina(console.lerTexto("Código da disciplina"));
                        console.sucesso("Disciplina removida.");
                    }
                    default -> { }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void cadastrarDisciplina() {
        String codigo = console.lerTexto("Código");
        String nome = console.lerTexto("Nome");
        TipoDisciplina tipo = lerTipo(null);
        listarCursos();
        String curso = console.lerTexto("Código do curso");
        listarUsuarios(controller.listarProfessores());
        String professor = console.lerTexto("Identificador do professor");
        Disciplina disciplina = controller.cadastrarDisciplina(codigo, nome, tipo, curso, professor);
        console.sucesso("Disciplina " + disciplina.getNome() + " cadastrada.");
    }

    private void alterarDisciplina() {
        listarDisciplinas();
        Disciplina d = controller.buscarDisciplina(console.lerTexto("Código da disciplina"));
        String nome = console.lerTexto("Nome", d.getNome());
        TipoDisciplina tipo = lerTipo(d.getTipo());
        listarUsuarios(controller.listarProfessores());
        String professor = console.lerTexto("Identificador do professor", d.getProfessor().getId());
        controller.alterarDisciplina(d.getCodigo(), nome, tipo, professor);
        console.sucesso("Disciplina alterada.");
    }

    private TipoDisciplina lerTipo(TipoDisciplina atual) {
        while (true) {
            String rotulo = "Tipo (1 = obrigatória, 2 = optativa)";
            String valor = atual == null ? console.lerTexto(rotulo) : console.lerTexto(rotulo, tipoParaOpcao(atual));
            if (valor.equals("1")) {
                return TipoDisciplina.OBRIGATORIA;
            }
            if (valor.equals("2")) {
                return TipoDisciplina.OPTATIVA;
            }
            console.erro("Informe 1 ou 2.");
        }
    }

    private static String tipoParaOpcao(TipoDisciplina tipo) {
        return tipo == TipoDisciplina.OBRIGATORIA ? "1" : "2";
    }

    private void listarDisciplinas() {
        imprimirDisciplinas(controller.listarDisciplinas(), null);
    }

    /** Lista disciplinas; com semestre informado, inclui o total de matriculados. */
    private void imprimirDisciplinas(List<Disciplina> disciplinas, String semestre) {
        if (disciplinas.isEmpty()) {
            console.linha("Nenhuma disciplina.");
            return;
        }
        console.linha("%-8s %-30s %-12s %-8s %-20s %-10s %s", "CÓDIGO", "NOME", "TIPO", "CURSO", "PROFESSOR",
                "SITUAÇÃO", semestre == null ? "" : "MATRIC.");
        for (Disciplina d : disciplinas) {
            console.linha("%-8s %-30s %-12s %-8s %-20s %-10s %s", d.getCodigo(), d.getNome(), d.getTipo(),
                    d.getCurso().getCodigo(), d.getProfessor().getNome(), d.getSituacao(),
                    semestre == null ? "" : controller.totalMatriculados(d, semestre) + "/"
                            + Disciplina.getLimiteMaximoAlunos());
        }
    }

    // ------------------------------------------------------------------ UC04 / UC05

    private void menuProfessores() {
        while (true) {
            console.titulo("PROFESSORES");
            int opcao = console.menu("Listar", "Cadastrar", "Alterar", "Remover", "Voltar");
            if (opcao == 0) {
                return;
            }
            try {
                switch (opcao) {
                    case 1 -> listarUsuarios(controller.listarProfessores());
                    case 2 -> {
                        Professor p = controller.cadastrarProfessor(console.lerTexto("Identificador (login)"),
                                console.lerTexto("Nome"), console.lerTexto("Senha"));
                        console.sucesso("Professor " + p.getNome() + " cadastrado.");
                    }
                    case 3 -> {
                        listarUsuarios(controller.listarProfessores());
                        Professor p = controller.buscarProfessor(console.lerTexto("Identificador"));
                        controller.alterarProfessor(p.getId(), console.lerTexto("Nome", p.getNome()),
                                console.lerTexto("Senha", p.getSenha()));
                        console.sucesso("Professor alterado.");
                    }
                    case 4 -> {
                        listarUsuarios(controller.listarProfessores());
                        controller.removerProfessor(console.lerTexto("Identificador"));
                        console.sucesso("Professor removido.");
                    }
                    default -> { }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void menuAlunos() {
        while (true) {
            console.titulo("ALUNOS");
            int opcao = console.menu("Listar", "Cadastrar", "Alterar", "Remover", "Voltar");
            if (opcao == 0) {
                return;
            }
            try {
                switch (opcao) {
                    case 1 -> listarUsuarios(controller.listarAlunos());
                    case 2 -> {
                        Aluno a = controller.cadastrarAluno(console.lerTexto("Matrícula (login)"),
                                console.lerTexto("Nome"), console.lerTexto("Senha"));
                        console.sucesso("Aluno " + a.getNome() + " cadastrado.");
                    }
                    case 3 -> {
                        listarUsuarios(controller.listarAlunos());
                        Aluno a = controller.buscarAluno(console.lerTexto("Matrícula"));
                        controller.alterarAluno(a.getId(), console.lerTexto("Nome", a.getNome()),
                                console.lerTexto("Senha", a.getSenha()));
                        console.sucesso("Aluno alterado.");
                    }
                    case 4 -> {
                        listarUsuarios(controller.listarAlunos());
                        controller.removerAluno(console.lerTexto("Matrícula"));
                        console.sucesso("Aluno removido.");
                    }
                    default -> { }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void listarUsuarios(List<? extends Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            console.linha("Nenhum registro.");
            return;
        }
        console.linha("%-12s %s", "ID", "NOME");
        for (Usuario u : usuarios) {
            console.linha("%-12s %s", u.getId(), u.getNome());
        }
    }

    // ------------------------------------------------------------------ UC06

    private void menuCurriculo() {
        while (true) {
            console.titulo("CURRÍCULO DO SEMESTRE");
            exibirVigente();
            int opcao = console.menu("Listar currículos", "Gerar currículo de novo semestre",
                    "Adicionar disciplina ao currículo vigente", "Remover disciplina do currículo vigente",
                    "Alterar currículo vigente", "Voltar");
            if (opcao == 0) {
                return;
            }
            try {
                switch (opcao) {
                    case 1 -> listarCurriculos();
                    case 2 -> {
                        Curriculo c = controller.gerarCurriculo(console.lerTexto("Semestre (ex.: 2026/2)"));
                        console.sucesso("Currículo " + c.getSemestre() + " gerado e definido como vigente.");
                    }
                    case 3 -> {
                        String semestre = controller.obterCurriculoVigente().getSemestre();
                        listarDisciplinas();
                        controller.adicionarDisciplinaAoCurriculo(semestre, console.lerTexto("Código da disciplina"));
                        console.sucesso("Disciplina adicionada ao currículo " + semestre + ".");
                    }
                    case 4 -> {
                        Curriculo vigente = controller.obterCurriculoVigente();
                        imprimirDisciplinas(vigente.getDisciplinasOfertadas(), vigente.getSemestre());
                        controller.removerDisciplinaDoCurriculo(vigente.getSemestre(),
                                console.lerTexto("Código da disciplina"));
                        console.sucesso("Disciplina removida do currículo.");
                    }
                    case 5 -> {
                        listarCurriculos();
                        controller.definirCurriculoVigente(console.lerTexto("Semestre"));
                        console.sucesso("Currículo vigente alterado.");
                    }
                    default -> { }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void exibirVigente() {
        try {
            Curriculo vigente = controller.obterCurriculoVigente();
            console.linha("Currículo vigente: " + vigente.getSemestre());
            imprimirDisciplinas(vigente.getDisciplinasOfertadas(), vigente.getSemestre());
        } catch (RegraNegocioException e) {
            console.linha(e.getMessage());
        }
    }

    private void listarCurriculos() {
        List<Curriculo> curriculos = controller.listarCurriculos();
        if (curriculos.isEmpty()) {
            console.linha("Nenhum currículo gerado.");
        }
        for (Curriculo c : curriculos) {
            String codigos = c.getDisciplinasOfertadas().stream()
                    .map(Disciplina::getCodigo)
                    .collect(Collectors.joining(", "));
            console.linha("%-8s disciplinas: %s", c.getSemestre(), codigos.isEmpty() ? "-" : codigos);
        }
    }

    // ------------------------------------------------------------------ UC07 / UC12

    private void menuPeriodo() {
        while (true) {
            console.titulo("PERÍODO DE MATRÍCULAS");
            Curriculo vigente;
            try {
                vigente = controller.obterCurriculoVigente();
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
                return;
            }
            exibirPeriodo(vigente);
            int opcao = console.menu("Definir datas do período", "Abrir período de matrículas",
                    "Encerrar período (ativar/cancelar disciplinas)", "Voltar");
            if (opcao == 0) {
                return;
            }
            try {
                switch (opcao) {
                    case 1 -> {
                        controller.definirPeriodoMatriculas(vigente.getSemestre(), console.lerData("Data de início"),
                                console.lerData("Data de fim"));
                        console.sucesso("Período definido. Use a opção 2 para abri-lo.");
                    }
                    case 2 -> {
                        controller.abrirPeriodoMatriculas(vigente.getSemestre());
                        console.sucesso("Período de matrículas aberto.");
                    }
                    case 3 -> encerrarPeriodo(vigente);
                    default -> { }
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }

    private void exibirPeriodo(Curriculo curriculo) {
        PeriodoMatricula periodo = curriculo.getPeriodoMatricula();
        if (periodo == null) {
            console.linha("Semestre %s: período de matrículas ainda não definido.", curriculo.getSemestre());
            return;
        }
        console.linha("Semestre %s: %s a %s - %s", curriculo.getSemestre(), Console.formatar(periodo.getDataInicio()),
                Console.formatar(periodo.getDataFim()),
                controller.periodoAberto(curriculo) ? "ABERTO" : periodo.isAberto() ? "aberto (fora das datas)"
                        : "FECHADO");
    }

    private void encerrarPeriodo(Curriculo vigente) {
        if (!console.confirmar("Encerrar o período e processar as disciplinas de " + vigente.getSemestre() + "?")) {
            return;
        }
        List<Disciplina> disciplinas = controller.encerrarPeriodoMatriculas(vigente.getSemestre());
        console.titulo("RELATÓRIO DE ENCERRAMENTO - " + vigente.getSemestre());
        imprimirDisciplinas(disciplinas, vigente.getSemestre());
        console.linha("Disciplinas com menos de %d alunos foram canceladas (RN02).",
                Disciplina.getMinimoAlunosAtivacao());
    }
}
