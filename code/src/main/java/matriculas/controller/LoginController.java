package matriculas.controller;

import matriculas.model.Usuario;
import matriculas.service.AutenticacaoService;

public class LoginController {

    private final AutenticacaoService autenticacaoService;

    public LoginController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    /** UC01: retorna o usuário autenticado ou lança RegraNegocioException. */
    public Usuario login(String id, String senha) {
        return autenticacaoService.autenticar(id, senha);
    }
}
