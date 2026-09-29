package matriculas.view;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Leitura e escrita no terminal, com validação básica de entrada. */
public class Console {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner entrada;
    private final PrintStream saida;

    public Console(InputStream entrada, PrintStream saida) {
        this.entrada = new Scanner(entrada, StandardCharsets.UTF_8);
        this.saida = saida;
    }

    public void titulo(String texto) {
        saida.println();
        saida.println("=".repeat(60));
        saida.println(" " + texto);
        saida.println("=".repeat(60));
    }

    public void linha(String texto) {
        saida.println(texto);
    }

    public void linha(String formato, Object... args) {
        saida.println(String.format(formato, args));
    }

    public void sucesso(String texto) {
        saida.println("[OK] " + texto);
    }

    public void erro(String texto) {
        saida.println("[ERRO] " + texto);
    }

    /** Exibe as opções numeradas (a última é sempre "0") e retorna a escolhida. */
    public int menu(String... opcoes) {
        saida.println();
        for (int i = 0; i < opcoes.length - 1; i++) {
            saida.println(" " + (i + 1) + " - " + opcoes[i]);
        }
        saida.println(" 0 - " + opcoes[opcoes.length - 1]);
        while (true) {
            int opcao = lerInteiro("Opção");
            if (opcao >= 0 && opcao < opcoes.length) {
                return opcao;
            }
            erro("Opção inválida.");
        }
    }

    public String lerTexto(String rotulo) {
        saida.print(rotulo + ": ");
        saida.flush();
        if (!entrada.hasNextLine()) {
            throw new EntradaEncerradaException();
        }
        return entrada.nextLine().trim();
    }

    /** Lê um texto; entrada vazia mantém o valor atual. */
    public String lerTexto(String rotulo, String valorAtual) {
        String valor = lerTexto(rotulo + " [" + valorAtual + "]");
        return valor.isEmpty() ? valorAtual : valor;
    }

    public int lerInteiro(String rotulo) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(rotulo));
            } catch (NumberFormatException e) {
                erro("Informe um número inteiro.");
            }
        }
    }

    public int lerInteiro(String rotulo, int valorAtual) {
        while (true) {
            String valor = lerTexto(rotulo + " [" + valorAtual + "]");
            if (valor.isEmpty()) {
                return valorAtual;
            }
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                erro("Informe um número inteiro.");
            }
        }
    }

    public LocalDate lerData(String rotulo) {
        while (true) {
            try {
                return LocalDate.parse(lerTexto(rotulo + " (dd/mm/aaaa)"), FORMATO_DATA);
            } catch (DateTimeParseException e) {
                erro("Data inválida. Use o formato dd/mm/aaaa.");
            }
        }
    }

    public boolean confirmar(String pergunta) {
        return lerTexto(pergunta + " (s/n)").equalsIgnoreCase("s");
    }

    public static String formatar(LocalDate data) {
        return data == null ? "-" : data.format(FORMATO_DATA);
    }
}
