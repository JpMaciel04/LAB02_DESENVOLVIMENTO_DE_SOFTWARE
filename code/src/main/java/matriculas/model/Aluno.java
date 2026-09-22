package matriculas.model;

import java.util.ArrayList;
import java.util.List;

public class Aluno extends Usuario {

    private List<Inscricao> inscricoes = new ArrayList<>();

    public Aluno(String id, String nome, String senha) {
        super(id, nome, senha);
    }

    public List<Inscricao> getInscricoes() {
        return inscricoes;
    }
}
