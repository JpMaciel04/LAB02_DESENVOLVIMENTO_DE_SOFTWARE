package matriculas.service;

import java.time.LocalDate;
import java.util.List;
import matriculas.model.Curriculo;
import matriculas.model.Disciplina;
import matriculas.model.PeriodoMatricula;
import matriculas.repository.CurriculoRepository;

public class PeriodoMatriculaService {

    private final CurriculoRepository curriculoRepository;
    private final CurriculoService curriculoService;
    private final DisciplinaService disciplinaService;

    public PeriodoMatriculaService(CurriculoRepository curriculoRepository, CurriculoService curriculoService,
            DisciplinaService disciplinaService) {
        this.curriculoRepository = curriculoRepository;
        this.curriculoService = curriculoService;
        this.disciplinaService = disciplinaService;
    }

    /** UC07: define as datas do período de matrículas do currículo (ainda fechado). */
    public PeriodoMatricula definir(String semestre, LocalDate inicio, LocalDate fim) {
        Curriculo curriculo = curriculoService.buscar(semestre);
        if (inicio == null || fim == null) {
            throw new RegraNegocioException("Informe as datas de início e de fim.");
        }
        if (!fim.isAfter(inicio)) {
            throw new RegraNegocioException("A data de fim deve ser posterior à data de início.");
        }
        PeriodoMatricula periodo = new PeriodoMatricula(inicio, fim);
        curriculo.setPeriodoMatricula(periodo);
        curriculoRepository.salvar(curriculo);
        return periodo;
    }

    public void abrir(String semestre) {
        Curriculo curriculo = curriculoService.buscar(semestre);
        PeriodoMatricula periodo = exigirPeriodo(curriculo);
        if (LocalDate.now().isAfter(periodo.getDataFim())) {
            throw new RegraNegocioException("A data de fim do período já passou; defina um novo período.");
        }
        periodo.setAberto(true);
        curriculoRepository.salvar(curriculo);
    }

    /** UC12: fecha o período e aplica RN02 a cada disciplina do currículo. */
    public List<Disciplina> encerrar(String semestre) {
        Curriculo curriculo = curriculoService.buscar(semestre);
        PeriodoMatricula periodo = exigirPeriodo(curriculo);
        if (!periodo.isAberto()) {
            throw new RegraNegocioException("O período de matrículas de " + semestre + " não está aberto.");
        }
        periodo.setAberto(false);
        for (Disciplina disciplina : curriculo.getDisciplinasOfertadas()) {
            disciplinaService.avaliarSituacao(disciplina, semestre);
        }
        curriculoRepository.salvar(curriculo);
        return curriculo.getDisciplinasOfertadas();
    }

    /** RN04: matrículas só são aceitas com o período aberto e dentro das datas definidas. */
    public boolean estaAberto(PeriodoMatricula periodo) {
        if (periodo == null || !periodo.isAberto()) {
            return false;
        }
        LocalDate hoje = LocalDate.now();
        return !hoje.isBefore(periodo.getDataInicio()) && !hoje.isAfter(periodo.getDataFim());
    }

    private PeriodoMatricula exigirPeriodo(Curriculo curriculo) {
        if (curriculo.getPeriodoMatricula() == null) {
            throw new RegraNegocioException("Defina as datas do período de matrículas de "
                    + curriculo.getSemestre() + " primeiro.");
        }
        return curriculo.getPeriodoMatricula();
    }
}
