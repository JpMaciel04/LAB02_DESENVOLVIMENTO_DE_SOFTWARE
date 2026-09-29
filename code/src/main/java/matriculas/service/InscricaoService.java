package matriculas.service;

import java.util.List;
import java.util.Optional;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;
import matriculas.model.Inscricao;
import matriculas.model.Matricula;
import matriculas.model.SituacaoMatricula;
import matriculas.repository.InscricaoRepository;

public class InscricaoService {

    private static final System.Logger LOG = System.getLogger(InscricaoService.class.getName());

    private final InscricaoRepository inscricaoRepository;
    private final SistemaCobranca sistemaCobranca;

    public InscricaoService(InscricaoRepository inscricaoRepository, SistemaCobranca sistemaCobranca) {
        this.inscricaoRepository = inscricaoRepository;
        this.sistemaCobranca = sistemaCobranca;
    }

    public Optional<Inscricao> buscar(Aluno aluno, String semestre) {
        return inscricaoRepository.buscar(aluno, semestre);
    }

    public Inscricao obterOuCriar(Aluno aluno, String semestre) {
        return buscar(aluno, semestre).orElseGet(() -> {
            Inscricao inscricao = new Inscricao(aluno, semestre);
            aluno.getInscricoes().add(inscricao);
            inscricaoRepository.salvar(inscricao);
            return inscricao;
        });
    }

    /** Qualquer alteração nas matrículas exige nova confirmação (e nova notificação de cobrança). */
    public void reabrir(Inscricao inscricao) {
        inscricao.setConcluida(false);
        inscricaoRepository.salvar(inscricao);
    }

    /**
     * RN05 / UC13: conclui a inscrição do semestre e notifica o sistema de cobranças.
     * Uma falha na notificação é registrada em log e não impede a gravação da inscrição.
     *
     * @return {@code true} se a notificação foi entregue ao sistema de cobranças
     */
    public boolean concluir(Inscricao inscricao) {
        List<Disciplina> disciplinas = inscricao.getMatriculas().stream()
                .filter(m -> m.getSituacao() == SituacaoMatricula.ATIVA)
                .map(Matricula::getDisciplina)
                .toList();
        if (disciplinas.isEmpty()) {
            throw new RegraNegocioException("Não há matrículas ativas para concluir a inscrição.");
        }
        inscricao.setConcluida(true);
        inscricaoRepository.salvar(inscricao);
        try {
            sistemaCobranca.notificar(inscricao.getAluno(), inscricao.getSemestre(), disciplinas);
            return true;
        } catch (RuntimeException e) {
            LOG.log(System.Logger.Level.WARNING, "Falha ao notificar o sistema de cobranças", e);
            return false;
        }
    }
}
