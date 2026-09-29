package matriculas.repository;

import java.nio.file.Path;
import java.time.LocalDate;
import matriculas.model.Disciplina;
import matriculas.model.Inscricao;
import matriculas.model.Matricula;
import matriculas.model.SituacaoMatricula;

/** Arquivo matriculas.csv: idAluno;codigoDisciplina;semestre;dataMatricula;situacao */
public class MatriculaRepository extends RepositorioArquivo<Matricula> {

    private final InscricaoRepository inscricaoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public MatriculaRepository(Path pastaDados, InscricaoRepository inscricaoRepository,
            DisciplinaRepository disciplinaRepository) {
        super(pastaDados.resolve("matriculas.csv"));
        this.inscricaoRepository = inscricaoRepository;
        this.disciplinaRepository = disciplinaRepository;
        carregar();
    }

    @Override
    protected String chave(Matricula m) {
        return m.getAluno().getId() + "|" + m.getDisciplina().getCodigo() + "|" + m.getSemestre();
    }

    @Override
    protected String[] paraCampos(Matricula m) {
        return new String[] { m.getAluno().getId(), m.getDisciplina().getCodigo(), m.getSemestre(),
                m.getDataMatricula().toString(), m.getSituacao().name() };
    }

    @Override
    protected Matricula deCampos(String[] c) {
        Inscricao inscricao = inscricaoRepository.buscar(InscricaoRepository.chave(c[0], c[2])).orElse(null);
        Disciplina disciplina = disciplinaRepository.buscar(c[1]).orElse(null);
        if (inscricao == null || disciplina == null) {
            return null;
        }
        Matricula matricula = new Matricula(inscricao.getAluno(), disciplina, c[2]);
        matricula.setDataMatricula(LocalDate.parse(c[3]));
        matricula.setSituacao(SituacaoMatricula.valueOf(c[4]));
        disciplina.getMatriculas().add(matricula);
        inscricao.getMatriculas().add(matricula);
        return matricula;
    }
}
