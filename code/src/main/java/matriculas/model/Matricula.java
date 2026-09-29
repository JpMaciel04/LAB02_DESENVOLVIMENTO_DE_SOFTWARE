package matriculas.model;

import java.time.LocalDate;

public class Matricula {

    private Aluno aluno;
    private Disciplina disciplina;
    private String semestre;
    private LocalDate dataMatricula;
    private SituacaoMatricula situacao;

    public Matricula(Aluno aluno, Disciplina disciplina, String semestre) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.semestre = semestre;
        this.dataMatricula = LocalDate.now();
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

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public SituacaoMatricula getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMatricula situacao) {
        this.situacao = situacao;
    }
}
