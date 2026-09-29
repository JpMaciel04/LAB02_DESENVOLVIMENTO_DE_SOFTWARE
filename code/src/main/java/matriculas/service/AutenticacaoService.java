package matriculas.service;

import matriculas.model.Usuario;
import matriculas.repository.UsuarioRepository;

public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** RN06: valida o login do usuário pela senha cadastrada. */
    public Usuario autenticar(String id, String senha) {
        return usuarioRepository.buscar(id == null ? "" : id.trim())
                .filter(usuario -> usuario.getSenha().equals(senha))
                .orElseThrow(() -> new RegraNegocioException("Usuário ou senha inválidos."));
    }
}
