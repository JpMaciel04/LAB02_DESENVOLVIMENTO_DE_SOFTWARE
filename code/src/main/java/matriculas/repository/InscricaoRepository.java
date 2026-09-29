package matriculas.repository;

import java.nio.file.Path;
import java.util.Optional;
import matriculas.model.Aluno;
import matriculas.model.Inscricao;
import matriculas.model.Usuario;

/** Arquivo inscricoes.csv: idAluno;semestre;concluida */
public class InscricaoRepository extends RepositorioArquivo<Inscricao> {

    private final UsuarioRepository usuarioRepository;

    public InscricaoRepository(Path pastaDados, UsuarioRepository usuarioRepository) {
        super(pastaDados.resolve("inscricoes.csv"));
        this.usuarioRepository = usuarioRepository;
        carregar();
    }

    public Optional<Inscricao> buscar(Aluno aluno, String semestre) {
        return buscar(chave(aluno.getId(), semestre));
    }

    static String chave(String idAluno, String semestre) {
        return idAluno + "|" + semestre;
    }

    @Override
    protected String chave(Inscricao inscricao) {
        return chave(inscricao.getAluno().getId(), inscricao.getSemestre());
    }

    @Override
    protected String[] paraCampos(Inscricao i) {
        return new String[] { i.getAluno().getId(), i.getSemestre(), String.valueOf(i.isConcluida()) };
    }

    @Override
    protected Inscricao deCampos(String[] c) {
        Usuario usuario = usuarioRepository.buscar(c[0]).orElse(null);
        if (!(usuario instanceof Aluno aluno)) {
            return null;
        }
        Inscricao inscricao = new Inscricao(aluno, c[1]);
        inscricao.setConcluida(Boolean.parseBoolean(c[2]));
        aluno.getInscricoes().add(inscricao);
        return inscricao;
    }
}
