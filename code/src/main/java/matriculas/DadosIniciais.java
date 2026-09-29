package matriculas;

import java.time.LocalDate;
import matriculas.model.Secretaria;
import matriculas.model.TipoDisciplina;
import matriculas.service.MatriculaService;

/**
 * Carga inicial usada na primeira execução (pasta de dados vazia), para que o
 * protótipo possa ser demonstrado sem cadastros manuais. Tudo passa pelos
 * services, portanto respeita as mesmas regras de negócio da interface.
 */
public final class DadosIniciais {

    public static final String SEMESTRE = "2026/2";

    private DadosIniciais() {
    }

    public static void carregar(Aplicacao app) {
        app.getUsuarioRepository().salvar(new Secretaria("admin", "Secretaria Acadêmica", "admin"));

        app.getProfessorService().cadastrar("p1", "Ana Souza", "123");
        app.getProfessorService().cadastrar("p2", "Carlos Lima", "123");
        app.getAlunoService().cadastrar("a1", "Bruno Alves", "123");
        app.getAlunoService().cadastrar("a2", "Carla Dias", "123");
        app.getAlunoService().cadastrar("a3", "Diego Rocha", "123");
        app.getAlunoService().cadastrar("a4", "Elisa Martins", "123");
        app.getAlunoService().cadastrar("a5", "Felipe Nunes", "123");

        app.getCursoService().criar("ES", "Engenharia de Software", 240);
        criarDisciplina(app, "ES101", "Algoritmos e Estruturas de Dados", TipoDisciplina.OBRIGATORIA, "p1");
        criarDisciplina(app, "ES102", "Cálculo I", TipoDisciplina.OBRIGATORIA, "p2");
        criarDisciplina(app, "ES103", "Projeto de Software", TipoDisciplina.OBRIGATORIA, "p1");
        criarDisciplina(app, "ES104", "Banco de Dados", TipoDisciplina.OBRIGATORIA, "p2");
        criarDisciplina(app, "ES105", "Redes de Computadores", TipoDisciplina.OBRIGATORIA, "p2");
        criarDisciplina(app, "ES201", "Computação Gráfica", TipoDisciplina.OPTATIVA, "p1");
        criarDisciplina(app, "ES202", "Inteligência Artificial", TipoDisciplina.OPTATIVA, "p2");
        criarDisciplina(app, "ES203", "Libras", TipoDisciplina.OPTATIVA, "p1");

        app.getCurriculoService().gerar(SEMESTRE);
        for (String codigo : new String[] { "ES101", "ES102", "ES103", "ES104", "ES105", "ES201", "ES202", "ES203" }) {
            app.getCurriculoService().adicionarDisciplina(SEMESTRE, codigo);
        }
        LocalDate hoje = LocalDate.now();
        app.getPeriodoMatriculaService().definir(SEMESTRE, hoje.minusDays(1), hoje.plusDays(30));
        app.getPeriodoMatriculaService().abrir(SEMESTRE);

        // ES101 e ES103 atingem o mínimo de 3 alunos; ES102 e ES201 ficam abaixo (RN02).
        matricular(app, "a1", "ES101", "ES103", "ES201");
        matricular(app, "a2", "ES101", "ES103");
        matricular(app, "a3", "ES101", "ES103", "ES102");
    }

    private static void criarDisciplina(Aplicacao app, String codigo, String nome, TipoDisciplina tipo,
            String professor) {
        app.getDisciplinaService().criar(codigo, nome, tipo, "ES", professor);
    }

    private static void matricular(Aplicacao app, String idAluno, String... disciplinas) {
        MatriculaService matriculaService = app.getMatriculaService();
        var aluno = app.getAlunoService().buscar(idAluno);
        for (String codigo : disciplinas) {
            matriculaService.matricular(aluno, codigo);
        }
    }
}
