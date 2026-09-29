package matriculas.repository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import matriculas.model.Curriculo;
import matriculas.model.Disciplina;
import matriculas.model.PeriodoMatricula;

/**
 * Arquivo curriculos.csv:
 * semestre;vigente;dataInicio;dataFim;aberto;codigosDisciplinas(separados por vírgula)
 */
public class CurriculoRepository extends RepositorioArquivo<Curriculo> {

    private final DisciplinaRepository disciplinaRepository;
    private String semestreVigente;

    public CurriculoRepository(Path pastaDados, DisciplinaRepository disciplinaRepository) {
        super(pastaDados.resolve("curriculos.csv"));
        this.disciplinaRepository = disciplinaRepository;
        carregar();
    }

    public Optional<String> getSemestreVigente() {
        return Optional.ofNullable(semestreVigente);
    }

    public void definirVigente(Curriculo curriculo) {
        semestreVigente = curriculo.getSemestre();
        salvar(curriculo);
    }

    @Override
    protected String chave(Curriculo curriculo) {
        return curriculo.getSemestre();
    }

    @Override
    protected String[] paraCampos(Curriculo c) {
        PeriodoMatricula p = c.getPeriodoMatricula();
        String codigos = c.getDisciplinasOfertadas().stream()
                .map(Disciplina::getCodigo)
                .collect(Collectors.joining(","));
        return new String[] { c.getSemestre(), String.valueOf(c.getSemestre().equals(semestreVigente)),
                p == null ? "" : p.getDataInicio().toString(),
                p == null ? "" : p.getDataFim().toString(),
                p == null ? "false" : String.valueOf(p.isAberto()),
                codigos };
    }

    @Override
    protected Curriculo deCampos(String[] c) {
        Curriculo curriculo = new Curriculo(c[0]);
        if (Boolean.parseBoolean(c[1])) {
            semestreVigente = c[0];
        }
        if (!c[2].isEmpty()) {
            PeriodoMatricula periodo = new PeriodoMatricula(LocalDate.parse(c[2]), LocalDate.parse(c[3]));
            periodo.setAberto(Boolean.parseBoolean(c[4]));
            curriculo.setPeriodoMatricula(periodo);
        }
        if (!c[5].isEmpty()) {
            for (String codigo : c[5].split(",")) {
                disciplinaRepository.buscar(codigo).ifPresent(curriculo.getDisciplinasOfertadas()::add);
            }
        }
        return curriculo;
    }
}
