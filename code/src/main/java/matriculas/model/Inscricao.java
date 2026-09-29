package matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Inscricao {

    private Aluno aluno;
    private String semestre;
    private boolean concluida;
    private List<Matricula> matriculas = new ArrayList<>();

    public Inscricao(Aluno aluno, String semestre) {
        this.aluno = aluno;
        this.semestre = semestre;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public String getSemestre() {
        return semestre;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}
