package matriculas;

import java.nio.file.Path;
import matriculas.view.Console;
import matriculas.view.EntradaEncerradaException;
import matriculas.view.MenuAluno;
import matriculas.view.MenuPrincipal;
import matriculas.view.MenuProfessor;
import matriculas.view.MenuSecretaria;

/**
 * Ponto de entrada. Uso: {@code java -jar sistema-matriculas.jar [pastaDeDados]}
 * (padrão: ./dados).
 */
public class Main {

    public static void main(String[] args) {
        Path pastaDados = Path.of(args.length > 0 ? args[0] : "dados");
        Aplicacao app = new Aplicacao(pastaDados);
        Console console = new Console(System.in, System.out);

        if (app.semUsuarios()) {
            DadosIniciais.carregar(app);
            console.linha("Primeira execução: dados de demonstração criados em " + pastaDados.toAbsolutePath());
            console.linha("Logins: secretaria admin/admin | professores p1, p2 | alunos a1..a5 (senha 123)");
        }

        MenuPrincipal menu = new MenuPrincipal(console, app.getLoginController(),
                new MenuSecretaria(console, app.getSecretariaController()),
                new MenuAluno(console, app.getAlunoController()),
                new MenuProfessor(console, app.getProfessorController()));
        try {
            menu.executar();
        } catch (EntradaEncerradaException e) {
            console.linha("");
            console.linha("Entrada encerrada. Até logo!");
        }
    }
}
