package matriculas.view;

/** Sinaliza que a entrada padrão terminou (ex.: Ctrl+D / Ctrl+Z), encerrando o programa. */
public class EntradaEncerradaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntradaEncerradaException() {
        super("Entrada encerrada.");
    }
}
