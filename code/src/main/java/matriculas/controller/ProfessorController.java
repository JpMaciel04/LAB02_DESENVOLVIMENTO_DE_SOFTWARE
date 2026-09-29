package matriculas.controller;

import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;
import matriculas.model.Professor;
import matriculas.service.CurriculoService;
import matriculas.service.DisciplinaService;
import matriculas.service.ProfessorService;

public class ProfessorController {

    private final ProfessorService professorService;
    private final DisciplinaService disciplinaService;
    private final CurriculoService curriculoService;

    public ProfessorController(ProfessorService professorService, DisciplinaService disciplinaService,
            CurriculoService curriculoService) {
        this.professorService = professorService;
        this.disciplinaService = disciplinaService;
        this.curriculoService = curriculoService;
    }

    public List<Disciplina> listarMinhasDisciplinas(Professor professor) {
        return professor.getDisciplinas();
    }

    /** UC11: alunos matriculados no semestre vigente. */
    public List<Aluno> consultarAlunosMatriculados(Professor professor, String codigoDisciplina) {
        String semestre = curriculoService.obterVigente().getSemestre();
        return professorService.consultarAlunosMatriculados(professor, disciplinaService.buscar(codigoDisciplina),
                semestre);
    }
}
