package matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Disciplina {

    private static final int LIMITE_MAXIMO_ALUNOS = 60;
    private static final int MINIMO_ALUNOS_ATIVACAO = 3;

    private String nome;
    private TipoDisciplina tipo;
    private SituacaoDisciplina situacao;
    private Curso curso;
    private Professor professor;
    private List<Matricula> matriculas = new ArrayList<>();

    public Disciplina(String nome, TipoDisciplina tipo, Curso curso, Professor professor) {
        this.nome = nome;
        this.tipo = tipo;
        this.curso = curso;
        this.professor = professor;
        this.situacao = SituacaoDisciplina.ABERTA;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoDisciplina getTipo() {
        return tipo;
    }

    public void setTipo(TipoDisciplina tipo) {
        this.tipo = tipo;
    }

    public SituacaoDisciplina getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDisciplina situacao) {
        this.situacao = situacao;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }

    public static int getLimiteMaximoAlunos() {
        return LIMITE_MAXIMO_ALUNOS;
    }

    public static int getMinimoAlunosAtivacao() {
        return MINIMO_ALUNOS_ATIVACAO;
    }
}
