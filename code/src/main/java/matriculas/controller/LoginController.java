package matriculas.controller;

import matriculas.model.Usuario;
import matriculas.service.AutenticacaoService;

public class LoginController {

    private final AutenticacaoService autenticacaoService;

    public LoginController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    public Usuario login(String id, String senha) {
        return null;
    }
}
