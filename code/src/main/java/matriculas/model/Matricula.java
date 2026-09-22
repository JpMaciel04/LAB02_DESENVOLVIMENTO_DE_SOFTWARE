package matriculas.model;

import java.util.Date;

public class Matricula {

    private Aluno aluno;
    private Disciplina disciplina;
    private String semestre;
    private Date dataMatricula;
    private SituacaoMatricula situacao;

    public Matricula(Aluno aluno, Disciplina disciplina, String semestre) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.semestre = semestre;
        this.situacao = SituacaoMatricula.ATIVA;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public String getSemestre() {
        return semestre;
    }

    public Date getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(Date dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public SituacaoMatricula getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMatricula situacao) {
        this.situacao = situacao;
    }
}
