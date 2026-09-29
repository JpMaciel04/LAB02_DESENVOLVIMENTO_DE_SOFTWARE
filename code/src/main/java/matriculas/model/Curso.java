package matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Curso {

    private String codigo;
    private String nome;
    private int numeroCreditos;
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Curso(String codigo, String nome, int numeroCreditos) {
        this.codigo = codigo;
        this.nome = nome;
        this.numeroCreditos = numeroCreditos;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNumeroCreditos() {
        return numeroCreditos;
    }

    public void setNumeroCreditos(int numeroCreditos) {
        this.numeroCreditos = numeroCreditos;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
