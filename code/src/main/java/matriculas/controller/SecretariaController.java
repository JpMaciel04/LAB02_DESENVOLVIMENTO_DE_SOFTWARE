package matriculas.controller;

import java.util.Date;
import matriculas.model.Aluno;
import matriculas.model.Curriculo;
import matriculas.model.Curso;
import matriculas.model.Disciplina;
import matriculas.model.PeriodoMatricula;
import matriculas.model.Professor;
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

    public void manterCurso(Curso curso) {
    }

    public void manterDisciplina(Disciplina disciplina) {
    }

    public void manterProfessor(Professor professor) {
    }

    public void manterAluno(Aluno aluno) {
    }

    public Curriculo gerarCurriculo(String semestre) {
        return null;
    }

    public PeriodoMatricula definirPeriodoMatriculas(Date inicio, Date fim) {
        return null;
    }

    public void encerrarPeriodoMatriculas(PeriodoMatricula periodo) {
    }
}
