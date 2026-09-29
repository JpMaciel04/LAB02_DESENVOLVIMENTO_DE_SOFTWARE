package matriculas;

import java.nio.file.Path;
import matriculas.controller.AlunoController;
import matriculas.controller.LoginController;
import matriculas.controller.ProfessorController;
import matriculas.controller.SecretariaController;
import matriculas.repository.CurriculoRepository;
import matriculas.repository.CursoRepository;
import matriculas.repository.DisciplinaRepository;
import matriculas.repository.InscricaoRepository;
import matriculas.repository.MatriculaRepository;
import matriculas.repository.UsuarioRepository;
import matriculas.service.AlunoService;
import matriculas.service.AutenticacaoService;
import matriculas.service.CurriculoService;
import matriculas.service.CursoService;
import matriculas.service.DisciplinaService;
import matriculas.service.InscricaoService;
import matriculas.service.MatriculaService;
import matriculas.service.PeriodoMatriculaService;
import matriculas.service.ProfessorService;
import matriculas.service.SistemaCobranca;
import matriculas.service.SistemaCobrancaService;

/**
 * Monta as camadas do sistema (repository -> service -> controller) a partir
 * da pasta onde os arquivos de dados são gravados.
 */
public class Aplicacao {

    private final UsuarioRepository usuarioRepository;

    private final CursoService cursoService;
    private final ProfessorService professorService;
    private final AlunoService alunoService;
    private final DisciplinaService disciplinaService;
    private final CurriculoService curriculoService;
    private final PeriodoMatriculaService periodoMatriculaService;
    private final InscricaoService inscricaoService;
    private final MatriculaService matriculaService;

    private final LoginController loginController;
    private final SecretariaController secretariaController;
    private final AlunoController alunoController;
    private final ProfessorController professorController;

    public Aplicacao(Path pastaDados) {
        this(pastaDados, new SistemaCobrancaService(pastaDados.resolve("cobrancas.log")));
    }

    public Aplicacao(Path pastaDados, SistemaCobranca sistemaCobranca) {
        // Repositórios: a ordem respeita as referências entre os arquivos.
        usuarioRepository = new UsuarioRepository(pastaDados);
        CursoRepository cursoRepository = new CursoRepository(pastaDados);
        DisciplinaRepository disciplinaRepository = new DisciplinaRepository(pastaDados, cursoRepository,
                usuarioRepository);
        CurriculoRepository curriculoRepository = new CurriculoRepository(pastaDados, disciplinaRepository);
        InscricaoRepository inscricaoRepository = new InscricaoRepository(pastaDados, usuarioRepository);
        MatriculaRepository matriculaRepository = new MatriculaRepository(pastaDados, inscricaoRepository,
                disciplinaRepository);

        // Services
        AutenticacaoService autenticacaoService = new AutenticacaoService(usuarioRepository);
        cursoService = new CursoService(cursoRepository);
        professorService = new ProfessorService(usuarioRepository);
        alunoService = new AlunoService(usuarioRepository, inscricaoRepository, matriculaRepository);
        disciplinaService = new DisciplinaService(disciplinaRepository, curriculoRepository, matriculaRepository,
                cursoService, professorService);
        curriculoService = new CurriculoService(curriculoRepository, disciplinaRepository, disciplinaService);
        periodoMatriculaService = new PeriodoMatriculaService(curriculoRepository, curriculoService,
                disciplinaService);
        inscricaoService = new InscricaoService(inscricaoRepository, sistemaCobranca);
        matriculaService = new MatriculaService(matriculaRepository, curriculoService, periodoMatriculaService,
                disciplinaService, inscricaoService);

        // Controllers
        loginController = new LoginController(autenticacaoService);
        secretariaController = new SecretariaController(cursoService, disciplinaService, professorService,
                alunoService, curriculoService, periodoMatriculaService);
        alunoController = new AlunoController(curriculoService, periodoMatriculaService, disciplinaService,
                matriculaService, inscricaoService);
        professorController = new ProfessorController(professorService, disciplinaService, curriculoService);
    }

    public boolean semUsuarios() {
        return usuarioRepository.listar().isEmpty();
    }

    public UsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }

    public CursoService getCursoService() {
        return cursoService;
    }

    public ProfessorService getProfessorService() {
        return professorService;
    }

    public AlunoService getAlunoService() {
        return alunoService;
    }

    public DisciplinaService getDisciplinaService() {
        return disciplinaService;
    }

    public CurriculoService getCurriculoService() {
        return curriculoService;
    }

    public PeriodoMatriculaService getPeriodoMatriculaService() {
        return periodoMatriculaService;
    }

    public InscricaoService getInscricaoService() {
        return inscricaoService;
    }

    public MatriculaService getMatriculaService() {
        return matriculaService;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public SecretariaController getSecretariaController() {
        return secretariaController;
    }

    public AlunoController getAlunoController() {
        return alunoController;
    }

    public ProfessorController getProfessorController() {
        return professorController;
    }
}
