package br.com.alura.runnercircleapi.service;

import br.com.alura.runnercircleapi.exception.ImagemInvalidaException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class ImagemStorageService {

    private static final long TAMANHO_MAXIMO_BYTES = 5L * 1024 * 1024;

    // extensão aceita -> content types aceitos para ela
    private static final Map<String, java.util.Set<String>> FORMATOS_ACEITOS = Map.of(
            "jpg", java.util.Set.of("image/jpeg"),
            "jpeg", java.util.Set.of("image/jpeg"),
            "png", java.util.Set.of("image/png"),
            "webp", java.util.Set.of("image/webp")
    );

    private final Path diretorioUploads;
    private final String urlBase;

    public ImagemStorageService(@Value("${app.uploads.dir:uploads}") String diretorio,
                                @Value("${app.uploads.url-base:/uploads}") String urlBase) {
        this.diretorioUploads = Paths.get(diretorio).toAbsolutePath().normalize();
        this.urlBase = urlBase;
    }

    /**
     * Valida e salva a imagem em disco com nome único e devolve a URL de acesso.
     */
    public String salvar(MultipartFile imagem) {
        String extensao = validar(imagem);
        String nomeArquivo = UUID.randomUUID() + "." + extensao;

        try {
            Files.createDirectories(diretorioUploads);
            Path destino = diretorioUploads.resolve(nomeArquivo);
            try (var entrada = imagem.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar a imagem", e);
        }

        return urlBase + "/" + nomeArquivo;
    }

    /**
     * Remove um arquivo salvo anteriormente a partir da sua URL (usado para desfazer em caso de falha).
     */
    public void remover(String imagemUrl) {
        if (imagemUrl == null || !imagemUrl.startsWith(urlBase + "/")) {
            return;
        }
        String nomeArquivo = imagemUrl.substring(urlBase.length() + 1);
        try {
            Files.deleteIfExists(diretorioUploads.resolve(nomeArquivo).normalize());
        } catch (IOException ignorada) {
            // melhor esforço: arquivo órfão não deve mascarar o erro original
        }
    }

    private String validar(MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty()) {
            throw new ImagemInvalidaException("a imagem enviada está vazia");
        }
        if (imagem.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new ImagemInvalidaException("a imagem deve ter no máximo 5 MB");
        }

        String extensao = extrairExtensao(imagem.getOriginalFilename());
        var contentTypesAceitos = FORMATOS_ACEITOS.get(extensao);
        if (contentTypesAceitos == null) {
            throw new ImagemInvalidaException("formato de imagem inválido: use jpg, jpeg, png ou webp");
        }

        String contentType = imagem.getContentType() == null
                ? ""
                : imagem.getContentType().toLowerCase(Locale.ROOT);
        if (!contentTypesAceitos.contains(contentType)) {
            throw new ImagemInvalidaException("tipo de conteúdo inválido para a imagem: " + contentType);
        }

        return extensao;
    }

    private String extrairExtensao(String nomeOriginal) {
        if (nomeOriginal == null) {
            return "";
        }
        int ponto = nomeOriginal.lastIndexOf('.');
        if (ponto < 0 || ponto == nomeOriginal.length() - 1) {
            return "";
        }
        return nomeOriginal.substring(ponto + 1).toLowerCase(Locale.ROOT);
    }
}