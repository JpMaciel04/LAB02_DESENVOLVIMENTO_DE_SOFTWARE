package matriculas.controller;

import java.time.LocalDate;
import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Curriculo;
import matriculas.model.Curso;
import matriculas.model.Disciplina;
import matriculas.model.PeriodoMatricula;
import matriculas.model.Professor;
import matriculas.model.TipoDisciplina;
import matriculas.service.AlunoService;
import matriculas.service.CurriculoService;
import matriculas.service.CursoService;
import matriculas.service.DisciplinaService;
import matriculas.service.PeriodoMatriculaService;
import matriculas.service.ProfessorService;

public class SecretariaController {

    private final CursoService cursoService;
    private final DisciplinaService disciplinaService;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final CurriculoService curriculoService;
    private final PeriodoMatriculaService periodoMatriculaService;

    public SecretariaController(CursoService cursoService, DisciplinaService disciplinaService,
            ProfessorService professorService, AlunoService alunoService,
            CurriculoService curriculoService, PeriodoMatriculaService periodoMatriculaService) {
        this.cursoService = cursoService;
        this.disciplinaService = disciplinaService;
        this.professorService = professorService;
        this.alunoService = alunoService;
        this.curriculoService = curriculoService;
        this.periodoMatriculaService = periodoMatriculaService;
    }

    // --- UC02: Manter Curso ---

    public Curso cadastrarCurso(String codigo, String nome, int numeroCreditos) {
        return cursoService.criar(codigo, nome, numeroCreditos);
    }

    public Curso alterarCurso(String codigo, String nome, int numeroCreditos) {
        return cursoService.atualizar(codigo, nome, numeroCreditos);
    }

    public void removerCurso(String codigo) {
        cursoService.remover(codigo);
    }

    public Curso buscarCurso(String codigo) {
        return cursoService.buscar(codigo);
    }

    public List<Curso> listarCursos() {
        return cursoService.listar();
    }

    // --- UC03: Manter Disciplina ---

    public Disciplina cadastrarDisciplina(String codigo, String nome, TipoDisciplina tipo, String codigoCurso,
            String idProfessor) {
        return disciplinaService.criar(codigo, nome, tipo, codigoCurso, idProfessor);
    }

    public Disciplina alterarDisciplina(String codigo, String nome, TipoDisciplina tipo, String idProfessor) {
        return disciplinaService.atualizar(codigo, nome, tipo, idProfessor);
    }

    public void removerDisciplina(String codigo) {
        disciplinaService.remover(codigo);
    }

    public Disciplina buscarDisciplina(String codigo) {
        return disciplinaService.buscar(codigo);
    }

    public List<Disciplina> listarDisciplinas() {
        return disciplinaService.listar();
    }

    public int totalMatriculados(Disciplina disciplina, String semestre) {
        return disciplinaService.totalMatriculados(disciplina, semestre);
    }

    // --- UC04: Manter Professor ---

    public Professor cadastrarProfessor(String id, String nome, String senha) {
        return professorService.cadastrar(id, nome, senha);
    }

    public Professor alterarProfessor(String id, String nome, String senha) {
        return professorService.atualizar(id, nome, senha);
    }

    public void removerProfessor(String id) {
        professorService.remover(id);
    }

    public Professor buscarProfessor(String id) {
        return professorService.buscar(id);
    }

    public List<Professor> listarProfessores() {
        return professorService.listar();
    }

    // --- UC05: Manter Aluno ---

    public Aluno cadastrarAluno(String id, String nome, String senha) {
        return alunoService.cadastrar(id, nome, senha);
    }

    public Aluno alterarAluno(String id, String nome, String senha) {
        return alunoService.atualizar(id, nome, senha);
    }

    public void removerAluno(String id) {
        alunoService.remover(id);
    }

    public Aluno buscarAluno(String id) {
        return alunoService.buscar(id);
    }

    public List<Aluno> listarAlunos() {
        return alunoService.listar();
    }

    // --- UC06: Gerar Currículo do Semestre ---

    public Curriculo gerarCurriculo(String semestre) {
        return curriculoService.gerar(semestre);
    }

    public void definirCurriculoVigente(String semestre) {
        curriculoService.definirVigente(semestre);
    }

    public void adicionarDisciplinaAoCurriculo(String semestre, String codigoDisciplina) {
        curriculoService.adicionarDisciplina(semestre, codigoDisciplina);
    }

    public void removerDisciplinaDoCurriculo(String semestre, String codigoDisciplina) {
        curriculoService.removerDisciplina(semestre, codigoDisciplina);
    }

    public List<Curriculo> listarCurriculos() {
        return curriculoService.listar();
    }

    public Curriculo obterCurriculoVigente() {
        return curriculoService.obterVigente();
    }

    // --- UC07 / UC12: Período de Matrículas ---

    public PeriodoMatricula definirPeriodoMatriculas(String semestre, LocalDate inicio, LocalDate fim) {
        return periodoMatriculaService.definir(semestre, inicio, fim);
    }

    public void abrirPeriodoMatriculas(String semestre) {
        periodoMatriculaService.abrir(semestre);
    }

    public boolean periodoAberto(Curriculo curriculo) {
        return periodoMatriculaService.estaAberto(curriculo.getPeriodoMatricula());
    }

    /** UC12: encerra o período e retorna as disciplinas com a situação final (RN02). */
    public List<Disciplina> encerrarPeriodoMatriculas(String semestre) {
        return periodoMatriculaService.encerrar(semestre);
    }
}
