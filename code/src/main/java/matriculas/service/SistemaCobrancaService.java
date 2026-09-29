package matriculas.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import matriculas.model.Aluno;
import matriculas.model.Disciplina;

/**
 * Integração simulada com o sistema de cobranças externo: cada notificação é
 * registrada como uma linha no arquivo de cobranças.
 */
public class SistemaCobrancaService implements SistemaCobranca {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Path arquivo;

    public SistemaCobrancaService(Path arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public void notificar(Aluno aluno, String semestre, List<Disciplina> disciplinas) {
        String codigos = disciplinas.stream().map(Disciplina::getCodigo).collect(Collectors.joining(","));
        String linha = String.join(";", LocalDateTime.now().format(FORMATO), aluno.getId(), aluno.getNome(),
                semestre, codigos) + System.lineSeparator();
        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            Files.writeString(arquivo, linha, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao notificar o sistema de cobranças", e);
        }
    }
}
