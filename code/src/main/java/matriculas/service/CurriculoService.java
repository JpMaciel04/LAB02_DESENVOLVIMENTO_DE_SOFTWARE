package matriculas.service;

import static matriculas.service.RegraNegocioException.exigirTexto;

import java.util.ArrayList;
import java.util.List;
import matriculas.model.Curriculo;
import matriculas.model.Disciplina;
import matriculas.model.SituacaoDisciplina;
import matriculas.repository.CurriculoRepository;
import matriculas.repository.DisciplinaRepository;

public class CurriculoService {

    private final CurriculoRepository curriculoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaService disciplinaService;

    public CurriculoService(CurriculoRepository curriculoRepository, DisciplinaRepository disciplinaRepository,
            DisciplinaService disciplinaService) {
        this.curriculoRepository = curriculoRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.disciplinaService = disciplinaService;
    }

    /** UC06: gera o currículo do semestre e o torna o currículo vigente para matrículas. */
    public Curriculo gerar(String semestre) {
        semestre = exigirTexto(semestre, "semestre");
        if (curriculoRepository.buscar(semestre).isPresent()) {
            throw new RegraNegocioException("Já existe currículo para o semestre " + semestre + ".");
        }
        Curriculo curriculo = new Curriculo(semestre);
        curriculoRepository.definirVigente(curriculo);
        return curriculo;
    }

    public void definirVigente(String semestre) {
        curriculoRepository.definirVigente(buscar(semestre));
    }

    public void adicionarDisciplina(String semestre, String codigoDisciplina) {
        Curriculo curriculo = buscar(semestre);
        Disciplina disciplina = disciplinaService.buscar(codigoDisciplina);
        if (curriculo.getDisciplinasOfertadas().contains(disciplina)) {
            throw new RegraNegocioException("A disciplina já faz parte do currículo de " + semestre + ".");
        }
        curriculo.getDisciplinasOfertadas().add(disciplina);
        disciplina.setSituacao(SituacaoDisciplina.ABERTA);
        disciplinaRepository.salvar(disciplina);
        curriculoRepository.salvar(curriculo);
    }

    public void removerDisciplina(String semestre, String codigoDisciplina) {
        Curriculo curriculo = buscar(semestre);
        Disciplina disciplina = disciplinaService.buscar(codigoDisciplina);
        if (!curriculo.getDisciplinasOfertadas().contains(disciplina)) {
            throw new RegraNegocioException("A disciplina não faz parte do currículo de " + semestre + ".");
        }
        if (disciplinaService.totalMatriculados(disciplina, semestre) > 0) {
            throw new RegraNegocioException("A disciplina já possui alunos matriculados neste semestre.");
        }
        curriculo.getDisciplinasOfertadas().remove(disciplina);
        curriculoRepository.salvar(curriculo);
    }

    public Curriculo buscar(String semestre) {
        return curriculoRepository.buscar(semestre)
                .orElseThrow(() -> new RegraNegocioException("Currículo do semestre '" + semestre + "' não encontrado."));
    }

    public List<Curriculo> listar() {
        return curriculoRepository.listar();
    }

    public Curriculo obterVigente() {
        String semestre = curriculoRepository.getSemestreVigente()
                .orElseThrow(() -> new RegraNegocioException("A secretaria ainda não gerou o currículo do semestre."));
        return buscar(semestre);
    }

    /** UC10: disciplinas do currículo que ainda recebem matrículas. */
    public List<Disciplina> listarDisciplinasDisponiveis(Curriculo curriculo) {
        List<Disciplina> disponiveis = new ArrayList<>();
        for (Disciplina disciplina : curriculo.getDisciplinasOfertadas()) {
            if (disciplina.getSituacao() == SituacaoDisciplina.ABERTA) {
                disponiveis.add(disciplina);
            }
        }
        return disponiveis;
    }
}
