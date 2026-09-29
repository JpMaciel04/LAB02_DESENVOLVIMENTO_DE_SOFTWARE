package matriculas.repository;

import java.nio.file.Path;
import matriculas.model.Curso;
import matriculas.model.Disciplina;
import matriculas.model.Professor;
import matriculas.model.SituacaoDisciplina;
import matriculas.model.TipoDisciplina;
import matriculas.model.Usuario;

/** Arquivo disciplinas.csv: codigo;nome;tipo;situacao;codigoCurso;idProfessor */
public class DisciplinaRepository extends RepositorioArquivo<Disciplina> {

    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    public DisciplinaRepository(Path pastaDados, CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository) {
        super(pastaDados.resolve("disciplinas.csv"));
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        carregar();
    }

    @Override
    protected String chave(Disciplina disciplina) {
        return disciplina.getCodigo();
    }

    @Override
    protected String[] paraCampos(Disciplina d) {
        return new String[] { d.getCodigo(), d.getNome(), d.getTipo().name(), d.getSituacao().name(),
                d.getCurso().getCodigo(), d.getProfessor().getId() };
    }

    @Override
    protected Disciplina deCampos(String[] c) {
        Curso curso = cursoRepository.buscar(c[4]).orElse(null);
        Usuario professor = usuarioRepository.buscar(c[5]).orElse(null);
        if (curso == null || !(professor instanceof Professor)) {
            return null;
        }
        Disciplina disciplina = new Disciplina(c[0], c[1], TipoDisciplina.valueOf(c[2]), curso, (Professor) professor);
        disciplina.setSituacao(SituacaoDisciplina.valueOf(c[3]));
        curso.getDisciplinas().add(disciplina);
        ((Professor) professor).getDisciplinas().add(disciplina);
        return disciplina;
    }
}
