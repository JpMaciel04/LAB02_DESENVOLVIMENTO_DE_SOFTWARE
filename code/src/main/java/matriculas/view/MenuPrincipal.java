package matriculas.view;

import matriculas.controller.LoginController;
import matriculas.model.Aluno;
import matriculas.model.Professor;
import matriculas.model.Secretaria;
import matriculas.model.Usuario;
import matriculas.service.RegraNegocioException;

/** Tela inicial: login (UC01) e encaminhamento para o menu do perfil do usuário. */
public class MenuPrincipal {

    private final Console console;
    private final LoginController loginController;
    private final MenuSecretaria menuSecretaria;
    private final MenuAluno menuAluno;
    private final MenuProfessor menuProfessor;

    public MenuPrincipal(Console console, LoginController loginController, MenuSecretaria menuSecretaria,
            MenuAluno menuAluno, MenuProfessor menuProfessor) {
        this.console = console;
        this.loginController = loginController;
        this.menuSecretaria = menuSecretaria;
        this.menuAluno = menuAluno;
        this.menuProfessor = menuProfessor;
    }

    public void executar() {
        while (true) {
            console.titulo("SISTEMA DE MATRÍCULAS - PUC Minas");
            if (console.menu("Entrar", "Sair do sistema") == 0) {
                console.linha("Até logo!");
                return;
            }
            try {
                Usuario usuario = loginController.login(console.lerTexto("Usuário"), console.lerTexto("Senha"));
                console.sucesso("Bem-vindo(a), " + usuario.getNome() + "!");
                if (usuario instanceof Secretaria secretaria) {
                    menuSecretaria.executar(secretaria);
                } else if (usuario instanceof Aluno aluno) {
                    menuAluno.executar(aluno);
                } else if (usuario instanceof Professor professor) {
                    menuProfessor.executar(professor);
                }
            } catch (RegraNegocioException e) {
                console.erro(e.getMessage());
            }
        }
    }
}
