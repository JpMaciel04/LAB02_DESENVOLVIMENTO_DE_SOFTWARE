package matriculas.service;

import matriculas.model.Inscricao;

public class InscricaoService {

    private final SistemaCobranca sistemaCobranca;

    public InscricaoService(SistemaCobranca sistemaCobranca) {
        this.sistemaCobranca = sistemaCobranca;
    }

    public void concluir(Inscricao inscricao) {
    }
}
