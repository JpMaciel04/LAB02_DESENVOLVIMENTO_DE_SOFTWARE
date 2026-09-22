package matriculas.controller;

import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;
import matriculas.model.Professor;
import matriculas.service.ProfessorService;

public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    public List<Aluno> consultarAlunosMatriculados(Professor professor, Disciplina disciplina) {
        return null;
    }
}
