package matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Curriculo {

    private String semestre;
    private List<Disciplina> disciplinasOfertadas = new ArrayList<>();
    private PeriodoMatricula periodoMatricula;

    public Curriculo(String semestre) {
        this.semestre = semestre;
    }

    public String getSemestre() {
        return semestre;
    }

    public List<Disciplina> getDisciplinasOfertadas() {
        return disciplinasOfertadas;
    }

    public PeriodoMatricula getPeriodoMatricula() {
        return periodoMatricula;
    }

    public void setPeriodoMatricula(PeriodoMatricula periodoMatricula) {
        this.periodoMatricula = periodoMatricula;
    }
}
