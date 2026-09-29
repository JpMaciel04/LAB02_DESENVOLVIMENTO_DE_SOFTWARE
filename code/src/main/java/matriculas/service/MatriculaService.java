package matriculas.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import matriculas.model.Aluno;
import matriculas.model.Curriculo;
import matriculas.model.Disciplina;
import matriculas.model.Inscricao;
import matriculas.model.Matricula;
import matriculas.model.SituacaoDisciplina;
import matriculas.model.SituacaoMatricula;
import matriculas.model.TipoDisciplina;
import matriculas.repository.MatriculaRepository;

public class MatriculaService {

    private static final int MAXIMO_OBRIGATORIAS = 4;
    private static final int MAXIMO_OPTATIVAS = 2;

    private final MatriculaRepository matriculaRepository;
    private final CurriculoService curriculoService;
    private final PeriodoMatriculaService periodoMatriculaService;
    private final DisciplinaService disciplinaService;
    private final InscricaoService inscricaoService;

    public MatriculaService(MatriculaRepository matriculaRepository, CurriculoService curriculoService,
            PeriodoMatriculaService periodoMatriculaService, DisciplinaService disciplinaService,
            InscricaoService inscricaoService) {
        this.matriculaRepository = matriculaRepository;
        this.curriculoService = curriculoService;
        this.periodoMatriculaService = periodoMatriculaService;
        this.disciplinaService = disciplinaService;
        this.inscricaoService = inscricaoService;
    }

    /** UC08: matricula o aluno em uma disciplina do currículo vigente (RN01, RN03, RN04). */
    public Matricula matricular(Aluno aluno, String codigoDisciplina) {
        Curriculo curriculo = curriculoService.obterVigente();
        String semestre = curriculo.getSemestre();
        exigirPeriodoAberto(curriculo);

        Disciplina disciplina = disciplinaService.buscar(codigoDisciplina);
        if (!curriculo.getDisciplinasOfertadas().contains(disciplina)) {
            throw new RegraNegocioException("A disciplina não é ofertada no currículo de " + semestre + ".");
        }
        if (disciplina.getSituacao() != SituacaoDisciplina.ABERTA) {
            throw new RegraNegocioException("A disciplina não está aberta para matrículas.");
        }

        Inscricao inscricao = inscricaoService.obterOuCriar(aluno, semestre);
        List<Matricula> ativas = matriculasAtivas(inscricao);
        if (ativas.stream().anyMatch(m -> m.getDisciplina() == disciplina)) {
            throw new RegraNegocioException("Você já está matriculado nesta disciplina.");
        }
        if (!disciplinaService.temVagas(disciplina, semestre)) {
            throw new RegraNegocioException("Inscrições encerradas: a disciplina atingiu o limite de "
                    + Disciplina.getLimiteMaximoAlunos() + " alunos.");
        }
        long mesmoTipo = ativas.stream().filter(m -> m.getDisciplina().getTipo() == disciplina.getTipo()).count();
        int limite = disciplina.getTipo() == TipoDisciplina.OBRIGATORIA ? MAXIMO_OBRIGATORIAS : MAXIMO_OPTATIVAS;
        if (mesmoTipo >= limite) {
            throw new RegraNegocioException("Limite atingido: no máximo " + MAXIMO_OBRIGATORIAS
                    + " obrigatórias e " + MAXIMO_OPTATIVAS + " optativas por semestre.");
        }

        // Uma matrícula cancelada anteriormente na mesma disciplina é reativada.
        Optional<Matricula> cancelada = inscricao.getMatriculas().stream()
                .filter(m -> m.getDisciplina() == disciplina)
                .findFirst();
        Matricula matricula;
        if (cancelada.isPresent()) {
            matricula = cancelada.get();
            matricula.setSituacao(SituacaoMatricula.ATIVA);
            matricula.setDataMatricula(LocalDate.now());
        } else {
            matricula = new Matricula(aluno, disciplina, semestre);
            disciplina.getMatriculas().add(matricula);
            inscricao.getMatriculas().add(matricula);
        }
        matriculaRepository.salvar(matricula);
        inscricaoService.reabrir(inscricao);
        return matricula;
    }

    /** UC09: cancela a matrícula ativa do aluno na disciplina, liberando a vaga (RN04). */
    public void cancelar(Aluno aluno, String codigoDisciplina) {
        Curriculo curriculo = curriculoService.obterVigente();
        exigirPeriodoAberto(curriculo);

        Inscricao inscricao = inscricaoService.buscar(aluno, curriculo.getSemestre())
                .orElseThrow(() -> new RegraNegocioException("Você não possui matrículas neste semestre."));
        Matricula matricula = matriculasAtivas(inscricao).stream()
                .filter(m -> m.getDisciplina().getCodigo().equals(codigoDisciplina))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException(
                        "Você não possui matrícula ativa na disciplina '" + codigoDisciplina + "'."));
        matricula.setSituacao(SituacaoMatricula.CANCELADA);
        matriculaRepository.salvar(matricula);
        inscricaoService.reabrir(inscricao);
    }

    public List<Matricula> listarMatriculas(Aluno aluno, String semestre) {
        return inscricaoService.buscar(aluno, semestre)
                .map(Inscricao::getMatriculas)
                .orElse(List.of());
    }

    private void exigirPeriodoAberto(Curriculo curriculo) {
        if (!periodoMatriculaService.estaAberto(curriculo.getPeriodoMatricula())) {
            throw new RegraNegocioException("O período de matrículas de " + curriculo.getSemestre()
                    + " não está aberto.");
        }
    }

    private List<Matricula> matriculasAtivas(Inscricao inscricao) {
        return inscricao.getMatriculas().stream()
                .filter(m -> m.getSituacao() == SituacaoMatricula.ATIVA)
                .toList();
    }
}
