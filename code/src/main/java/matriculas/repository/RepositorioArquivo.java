package matriculas.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Base dos repositórios: mantém os registros em memória e os grava em um
 * arquivo texto (uma linha por registro, campos separados por ';').
 * Referências a outras entidades são gravadas pelo identificador e
 * resolvidas na carga pelos repositórios dos quais este depende.
 */
public abstract class RepositorioArquivo<T> {

    private static final char SEPARADOR = ';';
    private static final char ESCAPE = '\\';

    private final Path arquivo;
    private final Map<String, T> registros = new LinkedHashMap<>();

    protected RepositorioArquivo(Path arquivo) {
        this.arquivo = arquivo;
    }

    /** Identificador único do registro no arquivo. */
    protected abstract String chave(T entidade);

    protected abstract String[] paraCampos(T entidade);

    /** Reconstrói a entidade; retorna {@code null} se ela referenciar dados inexistentes. */
    protected abstract T deCampos(String[] campos);

    public void salvar(T entidade) {
        registros.put(chave(entidade), entidade);
        gravar();
    }

    public void remover(T entidade) {
        registros.remove(chave(entidade));
        gravar();
    }

    public Optional<T> buscar(String chave) {
        return Optional.ofNullable(registros.get(chave));
    }

    public List<T> listar() {
        return new ArrayList<>(registros.values());
    }

    /** Deve ser chamado pelo construtor da subclasse, depois de suas dependências estarem prontas. */
    protected final void carregar() {
        if (!Files.exists(arquivo)) {
            return;
        }
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                if (linha.isBlank()) {
                    continue;
                }
                T entidade = deCampos(dividir(linha));
                if (entidade != null) {
                    registros.put(chave(entidade), entidade);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler " + arquivo, e);
        }
    }

    protected final void gravar() {
        List<String> linhas = new ArrayList<>();
        for (T entidade : registros.values()) {
            linhas.add(juntar(paraCampos(entidade)));
        }
        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
            Files.write(temporario, linhas, StandardCharsets.UTF_8);
            Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gravar " + arquivo, e);
        }
    }

    private static String juntar(String[] campos) {
        StringBuilder linha = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                linha.append(SEPARADOR);
            }
            String campo = campos[i] == null ? "" : campos[i];
            for (char c : campo.toCharArray()) {
                if (c == SEPARADOR || c == ESCAPE) {
                    linha.append(ESCAPE).append(c);
                } else if (c == '\n' || c == '\r') {
                    linha.append(' ');
                } else {
                    linha.append(c);
                }
            }
        }
        return linha.toString();
    }

    private static String[] dividir(String linha) {
        List<String> campos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);
            if (c == ESCAPE && i + 1 < linha.length()) {
                atual.append(linha.charAt(++i));
            } else if (c == SEPARADOR) {
                campos.add(atual.toString());
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }
        campos.add(atual.toString());
        return campos.toArray(new String[0]);
    }
}
