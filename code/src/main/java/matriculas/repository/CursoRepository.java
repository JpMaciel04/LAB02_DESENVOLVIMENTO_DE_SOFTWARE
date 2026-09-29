package matriculas.repository;

import java.nio.file.Path;
import matriculas.model.Curso;

/** Arquivo cursos.csv: codigo;nome;numeroCreditos */
public class CursoRepository extends RepositorioArquivo<Curso> {

    public CursoRepository(Path pastaDados) {
        super(pastaDados.resolve("cursos.csv"));
        carregar();
    }

    @Override
    protected String chave(Curso curso) {
        return curso.getCodigo();
    }

    @Override
    protected String[] paraCampos(Curso curso) {
        return new String[] { curso.getCodigo(), curso.getNome(), String.valueOf(curso.getNumeroCreditos()) };
    }

    @Override
    protected Curso deCampos(String[] c) {
        return new Curso(c[0], c[1], Integer.parseInt(c[2]));
    }
}
