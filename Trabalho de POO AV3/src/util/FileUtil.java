package util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Utilitário simples para gravar linhas em arquivo de texto.
 */
public class FileUtil {

    /**
     * Grava as linhas no arquivo informado (cria/overwrite).
     *
     * @param linhas caminho do arquivo
     * @throws IOException em caso de erro de I/O
     */
    public static void escreverLinhasEmArquivo(List<String> linhas, Path caminho) throws IOException {
        // Certifica diretório existente
        if (caminho.getParent() != null) {
            Files.createDirectories(caminho.getParent());
        }
        try (BufferedWriter writer = Files.newBufferedWriter(caminho,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String linha : linhas) {
                writer.write(linha == null ? "" : linha);
                writer.newLine();
            }
        }
    }
}
