package matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Curriculo {

    private String semestre;
    private List<Disciplina> disciplinasOfertadas = new ArrayList<>();

    public Curriculo(String semestre) {
        this.semestre = semestre;
    }

    public String getSemestre() {
        return semestre;
    }

    public List<Disciplina> getDisciplinasOfertadas() {
        return disciplinasOfertadas;
    }
}
