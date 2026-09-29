package matriculas.repository;

import java.nio.file.Path;
import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Professor;
import matriculas.model.Secretaria;
import matriculas.model.Usuario;

/** Arquivo usuarios.csv: tipo;id;nome;senha */
public class UsuarioRepository extends RepositorioArquivo<Usuario> {

    public UsuarioRepository(Path pastaDados) {
        super(pastaDados.resolve("usuarios.csv"));
        carregar();
    }

    public <T extends Usuario> List<T> listarPorTipo(Class<T> tipo) {
        return listar().stream().filter(tipo::isInstance).map(tipo::cast).toList();
    }

    @Override
    protected String chave(Usuario usuario) {
        return usuario.getId();
    }

    @Override
    protected String[] paraCampos(Usuario usuario) {
        return new String[] { usuario.getClass().getSimpleName().toUpperCase(), usuario.getId(),
                usuario.getNome(), usuario.getSenha() };
    }

    @Override
    protected Usuario deCampos(String[] c) {
        return switch (c[0]) {
            case "ALUNO" -> new Aluno(c[1], c[2], c[3]);
            case "PROFESSOR" -> new Professor(c[1], c[2], c[3]);
            case "SECRETARIA" -> new Secretaria(c[1], c[2], c[3]);
            default -> null;
        };
    }
}
