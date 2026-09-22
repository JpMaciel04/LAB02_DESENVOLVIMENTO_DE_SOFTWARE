package matriculas.controller;

import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Curriculo;
import matriculas.model.Disciplina;
import matriculas.model.Matricula;
import matriculas.service.CurriculoService;
import matriculas.service.InscricaoService;
import matriculas.service.MatriculaService;

public class AlunoController {

    private final CurriculoService curriculoService;
    private final MatriculaService matriculaService;
    private final InscricaoService inscricaoService;

    public AlunoController(CurriculoService curriculoService, MatriculaService matriculaService,
            InscricaoService inscricaoService) {
        this.curriculoService = curriculoService;
        this.matriculaService = matriculaService;
        this.inscricaoService = inscricaoService;
    }

    public List<Disciplina> consultarDisciplinasDisponiveis(Curriculo curriculo) {
        return null;
    }

    public Matricula matricular(Aluno aluno, Disciplina disciplina, String semestre) {
        return null;
    }

    public void cancelarMatricula(Matricula matricula) {
    }

    public List<Matricula> consultarMinhasMatriculas(Aluno aluno, String semestre) {
        return null;
    }
}
