package matriculas.service;

import static matriculas.service.RegraNegocioException.exigirTexto;

import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Inscricao;
import matriculas.model.Matricula;
import matriculas.model.SituacaoMatricula;
import matriculas.repository.InscricaoRepository;
import matriculas.repository.MatriculaRepository;
import matriculas.repository.UsuarioRepository;

public class AlunoService {

    private final UsuarioRepository usuarioRepository;
    private final InscricaoRepository inscricaoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlunoService(UsuarioRepository usuarioRepository, InscricaoRepository inscricaoRepository,
            MatriculaRepository matriculaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public Aluno cadastrar(String id, String nome, String senha) {
        id = exigirTexto(id, "matrícula");
        if (usuarioRepository.buscar(id).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário com o identificador '" + id + "'.");
        }
        Aluno aluno = new Aluno(id, exigirTexto(nome, "nome"), exigirTexto(senha, "senha"));
        usuarioRepository.salvar(aluno);
        return aluno;
    }

    public Aluno atualizar(String id, String nome, String senha) {
        Aluno aluno = buscar(id);
        aluno.setNome(exigirTexto(nome, "nome"));
        aluno.setSenha(exigirTexto(senha, "senha"));
        usuarioRepository.salvar(aluno);
        return aluno;
    }

    /** Remove o aluno e seu histórico de matrículas canceladas; recusa se houver matrícula ativa. */
    public void remover(String id) {
        Aluno aluno = buscar(id);
        for (Inscricao inscricao : aluno.getInscricoes()) {
            for (Matricula matricula : inscricao.getMatriculas()) {
                if (matricula.getSituacao() == SituacaoMatricula.ATIVA) {
                    throw new RegraNegocioException("O aluno possui matrículas ativas e não pode ser removido.");
                }
            }
        }
        for (Inscricao inscricao : aluno.getInscricoes()) {
            for (Matricula matricula : inscricao.getMatriculas()) {
                matricula.getDisciplina().getMatriculas().remove(matricula);
                matriculaRepository.remover(matricula);
            }
            inscricaoRepository.remover(inscricao);
        }
        usuarioRepository.remover(aluno);
    }

    public Aluno buscar(String id) {
        return usuarioRepository.buscar(id)
                .filter(Aluno.class::isInstance)
                .map(Aluno.class::cast)
                .orElseThrow(() -> new RegraNegocioException("Aluno '" + id + "' não encontrado."));
    }

    public List<Aluno> listar() {
        return usuarioRepository.listarPorTipo(Aluno.class);
    }
}
