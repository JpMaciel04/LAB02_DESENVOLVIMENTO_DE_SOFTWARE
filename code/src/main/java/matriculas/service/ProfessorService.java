package matriculas.service;

import static matriculas.service.RegraNegocioException.exigirTexto;

import java.util.ArrayList;
import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;
import matriculas.model.Matricula;
import matriculas.model.Professor;
import matriculas.model.SituacaoMatricula;
import matriculas.repository.UsuarioRepository;

public class ProfessorService {

    private final UsuarioRepository usuarioRepository;

    public ProfessorService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Professor cadastrar(String id, String nome, String senha) {
        id = exigirTexto(id, "identificador");
        if (usuarioRepository.buscar(id).isPresent()) {
            throw new RegraNegocioException("Já existe um usuário com o identificador '" + id + "'.");
        }
        Professor professor = new Professor(id, exigirTexto(nome, "nome"), exigirTexto(senha, "senha"));
        usuarioRepository.salvar(professor);
        return professor;
    }

    public Professor atualizar(String id, String nome, String senha) {
        Professor professor = buscar(id);
        professor.setNome(exigirTexto(nome, "nome"));
        professor.setSenha(exigirTexto(senha, "senha"));
        usuarioRepository.salvar(professor);
        return professor;
    }

    public void remover(String id) {
        Professor professor = buscar(id);
        if (!professor.getDisciplinas().isEmpty()) {
            throw new RegraNegocioException(
                    "O professor leciona disciplinas; transfira-as a outro professor antes de removê-lo.");
        }
        usuarioRepository.remover(professor);
    }

    public Professor buscar(String id) {
        return usuarioRepository.buscar(id)
                .filter(Professor.class::isInstance)
                .map(Professor.class::cast)
                .orElseThrow(() -> new RegraNegocioException("Professor '" + id + "' não encontrado."));
    }

    public List<Professor> listar() {
        return usuarioRepository.listarPorTipo(Professor.class);
    }

    /** UC11: alunos com matrícula ativa no semestre em uma disciplina lecionada pelo professor. */
    public List<Aluno> consultarAlunosMatriculados(Professor professor, Disciplina disciplina, String semestre) {
        if (disciplina.getProfessor() != professor) {
            throw new RegraNegocioException("A disciplina '" + disciplina.getCodigo() + "' não é lecionada por você.");
        }
        List<Aluno> alunos = new ArrayList<>();
        for (Matricula matricula : disciplina.getMatriculas()) {
            if (matricula.getSituacao() == SituacaoMatricula.ATIVA && matricula.getSemestre().equals(semestre)) {
                alunos.add(matricula.getAluno());
            }
        }
        return alunos;
    }
}
