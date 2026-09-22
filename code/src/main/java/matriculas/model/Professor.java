package matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Professor extends Usuario {

    private List<Disciplina> disciplinas = new ArrayList<>();

    public Professor(String id, String nome, String senha) {
        super(id, nome, senha);
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
