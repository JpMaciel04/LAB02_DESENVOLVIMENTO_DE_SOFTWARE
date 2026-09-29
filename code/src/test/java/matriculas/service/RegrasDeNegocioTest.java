package matriculas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import matriculas.Aplicacao;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;
import matriculas.model.Inscricao;
import matriculas.model.SituacaoDisciplina;
import matriculas.model.SituacaoMatricula;
import matriculas.model.TipoDisciplina;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RegrasDeNegocioTest {

    private static final String SEMESTRE = "2026/2";

    @TempDir
    Path pasta;

    private final List<String> notificacoes = new ArrayList<>();
    private boolean cobrancaFora;
    private Aplicacao app;
    private Aluno aluno;

    @BeforeEach
    void preparar() {
        app = novaAplicacao();
        app.getProfessorService().cadastrar("p1", "Professor", "123");
        aluno = app.getAlunoService().cadastrar("a1", "Aluno", "123");
        app.getCursoService().criar("ES", "Engenharia de Software", 240);
        app.getCurriculoService().gerar(SEMESTRE);
        for (int i = 1; i <= 5; i++) {
            criarNoCurriculo("OB" + i, TipoDisciplina.OBRIGATORIA);
        }
        for (int i = 1; i <= 3; i++) {
            criarNoCurriculo("OP" + i, TipoDisciplina.OPTATIVA);
        }
        app.getPeriodoMatriculaService().definir(SEMESTRE, LocalDate.now().minusDays(1), LocalDate.now().plusDays(10));
        app.getPeriodoMatriculaService().abrir(SEMESTRE);
    }

    private Aplicacao novaAplicacao() {
        return new Aplicacao(pasta, (a, semestre, disciplinas) -> {
            if (cobrancaFora) {
                throw new IllegalStateException("fora do ar");
            }
            notificacoes.add(a.getId() + ":" + semestre + ":" + disciplinas.size());
        });
    }

    private void criarNoCurriculo(String codigo, TipoDisciplina tipo) {
        app.getDisciplinaService().criar(codigo, "Disciplina " + codigo, tipo, "ES", "p1");
        app.getCurriculoService().adicionarDisciplina(SEMESTRE, codigo);
    }

    private MatriculaService matriculas() {
        return app.getMatriculaService();
    }

    @Test
    void rn01_limitaQuatroObrigatoriasEDuasOptativas() {
        for (int i = 1; i <= 4; i++) {
            matriculas().matricular(aluno, "OB" + i);
        }
        matriculas().matricular(aluno, "OP1");
        matriculas().matricular(aluno, "OP2");

        assertThrows(RegraNegocioException.class, () -> matriculas().matricular(aluno, "OB5"));
        assertThrows(RegraNegocioException.class, () -> matriculas().matricular(aluno, "OP3"));
    }

    @Test
    void naoPermiteMatriculaDuplicada() {
        matriculas().matricular(aluno, "OB1");
        assertThrows(RegraNegocioException.class, () -> matriculas().matricular(aluno, "OB1"));
    }

    @Test
    void rn03_encerraInscricoesAoAtingirSessentaAlunos() {
        for (int i = 0; i < Disciplina.getLimiteMaximoAlunos(); i++) {
            Aluno outro = app.getAlunoService().cadastrar("x" + i, "Aluno " + i, "123");
            matriculas().matricular(outro, "OB1");
        }
        Disciplina ob1 = app.getDisciplinaService().buscar("OB1");
        assertFalse(app.getDisciplinaService().temVagas(ob1, SEMESTRE));
        assertThrows(RegraNegocioException.class, () -> matriculas().matricular(aluno, "OB1"));
    }

    @Test
    void rn04_recusaMatriculaECancelamentoComPeriodoFechado() {
        matriculas().matricular(aluno, "OB1");
        app.getPeriodoMatriculaService().encerrar(SEMESTRE);

        assertThrows(RegraNegocioException.class, () -> matriculas().matricular(aluno, "OB2"));
        assertThrows(RegraNegocioException.class, () -> matriculas().cancelar(aluno, "OB1"));
    }

    @Test
    void cancelamentoLiberaVagaEPermiteNovaMatricula() {
        Disciplina ob1 = app.getDisciplinaService().buscar("OB1");
        matriculas().matricular(aluno, "OB1");
        matriculas().cancelar(aluno, "OB1");
        assertEquals(0, app.getDisciplinaService().totalMatriculados(ob1, SEMESTRE));

        matriculas().matricular(aluno, "OB1");
        assertEquals(1, app.getDisciplinaService().totalMatriculados(ob1, SEMESTRE));
    }

    @Test
    void rn02_encerramentoAtivaComTresAlunosECancelaAsDemais() {
        for (String id : new String[] { "b1", "b2", "b3" }) {
            matriculas().matricular(app.getAlunoService().cadastrar(id, id, "123"), "OB1");
        }
        matriculas().matricular(aluno, "OB2");

        app.getPeriodoMatriculaService().encerrar(SEMESTRE);

        assertEquals(SituacaoDisciplina.ATIVA, app.getDisciplinaService().buscar("OB1").getSituacao());
        assertEquals(SituacaoDisciplina.CANCELADA, app.getDisciplinaService().buscar("OB2").getSituacao());
    }

    @Test
    void rn05_concluirInscricaoNotificaCobranca() {
        matriculas().matricular(aluno, "OB1");
        matriculas().matricular(aluno, "OP1");
        Inscricao inscricao = app.getInscricaoService().buscar(aluno, SEMESTRE).orElseThrow();

        assertTrue(app.getInscricaoService().concluir(inscricao));
        assertEquals(List.of("a1:2026/2:2"), notificacoes);
        assertTrue(inscricao.isConcluida());
    }

    @Test
    void falhaNaCobrancaNaoImpedeConclusao() {
        matriculas().matricular(aluno, "OB1");
        Inscricao inscricao = app.getInscricaoService().buscar(aluno, SEMESTRE).orElseThrow();
        cobrancaFora = true;

        assertFalse(app.getInscricaoService().concluir(inscricao));
        assertTrue(inscricao.isConcluida());
    }

    @Test
    void rn06_loginExigeSenhaCorreta() {
        assertNotNull(app.getLoginController().login("a1", "123"));
        assertThrows(RegraNegocioException.class, () -> app.getLoginController().login("a1", "errada"));
        assertThrows(RegraNegocioException.class, () -> app.getLoginController().login("naoexiste", "123"));
    }

    @Test
    void naoRemoveDisciplinaComMatriculaAtiva() {
        matriculas().matricular(aluno, "OB1");
        assertThrows(RegraNegocioException.class, () -> app.getDisciplinaService().remover("OB1"));
    }

    @Test
    void dadosSobrevivemAReinicializacao() {
        matriculas().matricular(aluno, "OB1");
        matriculas().matricular(aluno, "OB2");
        matriculas().cancelar(aluno, "OB2");

        Aplicacao recarregada = novaAplicacao();
        Aluno mesmoAluno = recarregada.getAlunoService().buscar("a1");
        Disciplina ob1 = recarregada.getDisciplinaService().buscar("OB1");

        assertEquals(SEMESTRE, recarregada.getCurriculoService().obterVigente().getSemestre());
        assertEquals(8, recarregada.getCurriculoService().obterVigente().getDisciplinasOfertadas().size());
        assertTrue(recarregada.getPeriodoMatriculaService()
                .estaAberto(recarregada.getCurriculoService().obterVigente().getPeriodoMatricula()));
        assertEquals(1, recarregada.getDisciplinaService().totalMatriculados(ob1, SEMESTRE));
        assertEquals(2, recarregada.getMatriculaService().listarMatriculas(mesmoAluno, SEMESTRE).size());
        assertEquals(SituacaoMatricula.CANCELADA, recarregada.getMatriculaService()
                .listarMatriculas(mesmoAluno, SEMESTRE).get(1).getSituacao());
        assertEquals(List.of(ob1), recarregada.getProfessorService().buscar("p1").getDisciplinas().subList(0, 1));
    }
}
