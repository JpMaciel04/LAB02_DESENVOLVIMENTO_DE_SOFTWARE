package matriculas.controller;

import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Curriculo;
import matriculas.model.Disciplina;
import matriculas.model.Inscricao;
import matriculas.model.Matricula;
import matriculas.service.CurriculoService;
import matriculas.service.DisciplinaService;
import matriculas.service.InscricaoService;
import matriculas.service.MatriculaService;
import matriculas.service.PeriodoMatriculaService;
import matriculas.service.RegraNegocioException;

public class AlunoController {

    private final CurriculoService curriculoService;
    private final PeriodoMatriculaService periodoMatriculaService;
    private final DisciplinaService disciplinaService;
    private final MatriculaService matriculaService;
    private final InscricaoService inscricaoService;

    public AlunoController(CurriculoService curriculoService, PeriodoMatriculaService periodoMatriculaService,
            DisciplinaService disciplinaService, MatriculaService matriculaService,
            InscricaoService inscricaoService) {
        this.curriculoService = curriculoService;
        this.periodoMatriculaService = periodoMatriculaService;
        this.disciplinaService = disciplinaService;
        this.matriculaService = matriculaService;
        this.inscricaoService = inscricaoService;
    }

    public Curriculo obterCurriculoVigente() {
        return curriculoService.obterVigente();
    }

    public boolean periodoAberto() {
        return periodoMatriculaService.estaAberto(obterCurriculoVigente().getPeriodoMatricula());
    }

    /** UC10 */
    public List<Disciplina> consultarDisciplinasDisponiveis() {
        return curriculoService.listarDisciplinasDisponiveis(obterCurriculoVigente());
    }

    public int vagasRestantes(Disciplina disciplina) {
        return disciplinaService.vagasRestantes(disciplina, obterCurriculoVigente().getSemestre());
    }

    /** UC08 */
    public Matricula matricular(Aluno aluno, String codigoDisciplina) {
        return matriculaService.matricular(aluno, codigoDisciplina);
    }

    /** UC09 */
    public void cancelarMatricula(Aluno aluno, String codigoDisciplina) {
        matriculaService.cancelar(aluno, codigoDisciplina);
    }

    /** US11 */
    public List<Matricula> consultarMinhasMatriculas(Aluno aluno) {
        return matriculaService.listarMatriculas(aluno, obterCurriculoVigente().getSemestre());
    }

    public boolean inscricaoConcluida(Aluno aluno) {
        return inscricaoService.buscar(aluno, obterCurriculoVigente().getSemestre())
                .map(Inscricao::isConcluida)
                .orElse(false);
    }

    /** UC08 + UC13: confirma a inscrição do semestre e notifica o sistema de cobranças. */
    public boolean concluirInscricao(Aluno aluno) {
        Inscricao inscricao = inscricaoService.buscar(aluno, obterCurriculoVigente().getSemestre())
                .orElseThrow(() -> new RegraNegocioException("Você ainda não se matriculou neste semestre."));
        return inscricaoService.concluir(inscricao);
    }
}
