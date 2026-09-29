package matriculas.service;

import static matriculas.service.RegraNegocioException.exigirTexto;

import java.util.List;
import matriculas.model.Curso;
import matriculas.repository.CursoRepository;

public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public Curso criar(String codigo, String nome, int numeroCreditos) {
        codigo = exigirTexto(codigo, "código");
        if (cursoRepository.buscar(codigo).isPresent()) {
            throw new RegraNegocioException("Já existe um curso com o código '" + codigo + "'.");
        }
        Curso curso = new Curso(codigo, exigirTexto(nome, "nome"), validarCreditos(numeroCreditos));
        cursoRepository.salvar(curso);
        return curso;
    }

    public Curso atualizar(String codigo, String nome, int numeroCreditos) {
        Curso curso = buscar(codigo);
        curso.setNome(exigirTexto(nome, "nome"));
        curso.setNumeroCreditos(validarCreditos(numeroCreditos));
        cursoRepository.salvar(curso);
        return curso;
    }

    public void remover(String codigo) {
        Curso curso = buscar(codigo);
        if (!curso.getDisciplinas().isEmpty()) {
            throw new RegraNegocioException("O curso possui disciplinas; remova-as antes de remover o curso.");
        }
        cursoRepository.remover(curso);
    }

    public Curso buscar(String codigo) {
        return cursoRepository.buscar(codigo)
                .orElseThrow(() -> new RegraNegocioException("Curso '" + codigo + "' não encontrado."));
    }

    public List<Curso> listar() {
        return cursoRepository.listar();
    }

    private int validarCreditos(int numeroCreditos) {
        if (numeroCreditos <= 0) {
            throw new RegraNegocioException("O número de créditos deve ser maior que zero.");
        }
        return numeroCreditos;
    }
}
