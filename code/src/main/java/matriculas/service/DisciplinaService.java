package matriculas.service;

import static matriculas.service.RegraNegocioException.exigirTexto;

import java.util.List;
import matriculas.model.Curriculo;
import matriculas.model.Curso;
import matriculas.model.Disciplina;
import matriculas.model.Matricula;
import matriculas.model.Professor;
import matriculas.model.SituacaoDisciplina;
import matriculas.model.SituacaoMatricula;
import matriculas.model.TipoDisciplina;
import matriculas.repository.CurriculoRepository;
import matriculas.repository.DisciplinaRepository;
import matriculas.repository.MatriculaRepository;

public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final CurriculoRepository curriculoRepository;
    private final MatriculaRepository matriculaRepository;
    private final CursoService cursoService;
    private final ProfessorService professorService;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, CurriculoRepository curriculoRepository,
            MatriculaRepository matriculaRepository, CursoService cursoService, ProfessorService professorService) {
        this.disciplinaRepository = disciplinaRepository;
        this.curriculoRepository = curriculoRepository;
        this.matriculaRepository = matriculaRepository;
        this.cursoService = cursoService;
        this.professorService = professorService;
    }

    public Disciplina criar(String codigo, String nome, TipoDisciplina tipo, String codigoCurso, String idProfessor) {
        codigo = exigirTexto(codigo, "código");
        if (disciplinaRepository.buscar(codigo).isPresent()) {
            throw new RegraNegocioException("Já existe uma disciplina com o código '" + codigo + "'.");
        }
        exigirTipo(tipo);
        Curso curso = cursoService.buscar(codigoCurso);
        Professor professor = professorService.buscar(idProfessor);

        Disciplina disciplina = new Disciplina(codigo, exigirTexto(nome, "nome"), tipo, curso, professor);
        curso.getDisciplinas().add(disciplina);
        professor.getDisciplinas().add(disciplina);
        disciplinaRepository.salvar(disciplina);
        return disciplina;
    }

    public Disciplina atualizar(String codigo, String nome, TipoDisciplina tipo, String idProfessor) {
        Disciplina disciplina = buscar(codigo);
        exigirTipo(tipo);
        Professor novoProfessor = professorService.buscar(idProfessor);
        disciplina.setNome(exigirTexto(nome, "nome"));
        disciplina.setTipo(tipo);
        if (disciplina.getProfessor() != novoProfessor) {
            disciplina.getProfessor().getDisciplinas().remove(disciplina);
            novoProfessor.getDisciplinas().add(disciplina);
            disciplina.setProfessor(novoProfessor);
        }
        disciplinaRepository.salvar(disciplina);
        return disciplina;
    }

    /** Remove a disciplina (e seu histórico de matrículas canceladas); recusa se houver matrícula ativa. */
    public void remover(String codigo) {
        Disciplina disciplina = buscar(codigo);
        boolean possuiMatriculasAtivas = disciplina.getMatriculas().stream()
                .anyMatch(m -> m.getSituacao() == SituacaoMatricula.ATIVA);
        if (possuiMatriculasAtivas) {
            throw new RegraNegocioException("A disciplina possui matrículas ativas e não pode ser removida.");
        }
        for (Matricula matricula : disciplina.getMatriculas()) {
            matriculaRepository.remover(matricula);
        }
        for (Curriculo curriculo : curriculoRepository.listar()) {
            if (curriculo.getDisciplinasOfertadas().remove(disciplina)) {
                curriculoRepository.salvar(curriculo);
            }
        }
        disciplina.getCurso().getDisciplinas().remove(disciplina);
        disciplina.getProfessor().getDisciplinas().remove(disciplina);
        disciplinaRepository.remover(disciplina);
    }

    public Disciplina buscar(String codigo) {
        return disciplinaRepository.buscar(codigo)
                .orElseThrow(() -> new RegraNegocioException("Disciplina '" + codigo + "' não encontrada."));
    }

    public List<Disciplina> listar() {
        return disciplinaRepository.listar();
    }

    /** RN03: a disciplina aceita novas matrículas enquanto não atingir 60 alunos no semestre. */
    public boolean temVagas(Disciplina disciplina, String semestre) {
        return vagasRestantes(disciplina, semestre) > 0;
    }

    public int vagasRestantes(Disciplina disciplina, String semestre) {
        return Disciplina.getLimiteMaximoAlunos() - totalMatriculados(disciplina, semestre);
    }

    public int totalMatriculados(Disciplina disciplina, String semestre) {
        int total = 0;
        for (Matricula matricula : disciplina.getMatriculas()) {
            if (matricula.getSituacao() == SituacaoMatricula.ATIVA && matricula.getSemestre().equals(semestre)) {
                total++;
            }
        }
        return total;
    }

    /** RN02: ao final do período, a disciplina fica ativa com 3+ alunos; caso contrário, é cancelada. */
    public void avaliarSituacao(Disciplina disciplina, String semestre) {
        boolean atingiuMinimo = totalMatriculados(disciplina, semestre) >= Disciplina.getMinimoAlunosAtivacao();
        disciplina.setSituacao(atingiuMinimo ? SituacaoDisciplina.ATIVA : SituacaoDisciplina.CANCELADA);
        disciplinaRepository.salvar(disciplina);
    }

    private void exigirTipo(TipoDisciplina tipo) {
        if (tipo == null) {
            throw new RegraNegocioException("Informe se a disciplina é obrigatória ou optativa.");
        }
    }
}
