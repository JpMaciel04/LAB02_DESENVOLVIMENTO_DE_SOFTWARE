package matriculas.service;

/**
 * Violação de uma regra de negócio ou de uma validação de entrada. A mensagem
 * é exibida diretamente ao usuário pela interface.
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }

    static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException("O campo '" + campo + "' é obrigatório.");
        }
        return valor.trim();
    }
}
