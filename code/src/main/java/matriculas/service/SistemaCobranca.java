package matriculas.service;

import java.util.List;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;

public interface SistemaCobranca {

    void notificar(Aluno aluno, String semestre, List<Disciplina> disciplinas);
}
