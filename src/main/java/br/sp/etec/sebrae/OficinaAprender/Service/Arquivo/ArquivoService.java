package br.sp.etec.sebrae.OficinaAprender.Service.Arquivo;

import br.sp.etec.sebrae.OficinaAprender.Entity.Arquivo.Arquivo;
import br.sp.etec.sebrae.OficinaAprender.Repository.Arquivo.ArquivoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ArquivoService {
    public static final long MAXIMO = 10L * 1024 * 1024;
    public static final long TOTAL = 11L * 1024 * 1024;
    private final ArquivoRepository arquivos;
    public void validarLote(List<MultipartFile> lote) {
        if (lote == null) return;
        if (lote.size() > 10 || lote.stream().mapToLong(MultipartFile::getSize).sum() > TOTAL)
            throw erro(BAD_REQUEST, "Envie até 10 arquivos e até 11 MB no total.");
        lote.forEach(this::validarTamanho);
    }
    private void validarTamanho(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) throw erro(BAD_REQUEST, "Envie um arquivo válido.");
        if (arquivo.getSize() > MAXIMO) throw erro(PAYLOAD_TOO_LARGE, "Cada arquivo deve ter até 10 MB.");
    }
    @Transactional
    public Arquivo salvar(MultipartFile arquivo, boolean foto) {
        validarTamanho(arquivo);
        try {
            byte[] bytes = arquivo.getBytes();
            String nome = nome(arquivo.getOriginalFilename());
            String tipo = foto ? validarFoto(bytes) : tipoSeguro(bytes, nome);
            var salvo = new Arquivo();
            salvo.setNome(nome); salvo.setTipo(tipo); salvo.setConteudo(bytes); salvo.setTamanho(bytes.length);
            return arquivos.save(salvo);
        } catch (IOException e) { throw erro(BAD_REQUEST, "Não foi possível ler o arquivo."); }
    }
    public Arquivo buscar(Long id) {
        if (id == null) throw erro(NOT_FOUND, "Arquivo não encontrado.");
        return arquivos.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Arquivo não encontrado."));
    }
    @Transactional public void remover(Long id) { if (id != null) arquivos.deleteById(id); }
    public ResponseEntity<ByteArrayResource> resposta(Arquivo a, boolean inline) {
        var disposition = inline ? ContentDisposition.inline() : ContentDisposition.attachment();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(a.getTipo()))
                .contentLength(a.getTamanho()).cacheControl(CacheControl.noStore())
                .header("Content-Disposition", disposition.filename(a.getNome(), StandardCharsets.UTF_8).build().toString())
                .header("X-Content-Type-Options", "nosniff")
                .header("Content-Security-Policy", "sandbox; default-src 'none'")
                .body(new ByteArrayResource(a.getConteudo()));
    }
    public String nome(String nome) {
        String seguro = nome == null ? "arquivo" : nome.replace('\\', '/');
        seguro = seguro.substring(seguro.lastIndexOf('/') + 1).replaceAll("[\\p{Cntrl}]", "").trim();
        if (seguro.isBlank()) seguro = "arquivo";
        return seguro.substring(0, Math.min(seguro.length(), 200));
    }
    private String validarFoto(byte[] bytes) throws IOException {
        try (var entrada = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            var leitores = ImageIO.getImageReaders(entrada);
            if (!leitores.hasNext()) throw erro(BAD_REQUEST, "Envie uma foto PNG, JPEG ou GIF.");
            ImageReader leitor = leitores.next();
            try {
                leitor.setInput(entrada);
                String formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                String tipo = switch (formato) { case "png" -> "image/png"; case "jpeg", "jpg" -> "image/jpeg"; case "gif" -> "image/gif";
                    default -> throw erro(BAD_REQUEST, "Envie uma foto PNG, JPEG ou GIF."); };
                long pixels = (long) leitor.getWidth(0) * leitor.getHeight(0);
                if (pixels <= 0 || pixels > 25_000_000 || leitor.read(0) == null)
                    throw erro(BAD_REQUEST, "A foto deve ter até 25 milhões de pixels.");
                return tipo;
            } finally { leitor.dispose(); }
        }
    }
    private String tipoSeguro(byte[] bytes, String nome) {
        String extensao = nome.contains(".") ? nome.substring(nome.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
        if (!Set.of("pdf", "png", "jpg", "jpeg", "gif", "txt", "csv", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "zip", "mp3", "mp4", "wav", "ogg").contains(extensao))
            throw erro(BAD_REQUEST, "Formato de arquivo não permitido. Use documento, imagem, áudio ou vídeo.");
        if (extensao.equals("pdf") && !inicia(bytes, "%PDF-")) throw erro(BAD_REQUEST, "PDF inválido.");
        if (Set.of("png", "jpg", "jpeg", "gif").contains(extensao)) {
            try { return validarFoto(bytes); } catch (IOException e) { throw erro(BAD_REQUEST, "Imagem inválida."); }
        }
        if (Set.of("docx", "xlsx", "pptx", "zip").contains(extensao) && !inicia(bytes, "PK"))
            throw erro(BAD_REQUEST, "Documento compactado inválido.");
        if (Set.of("doc", "xls", "ppt").contains(extensao) && (bytes.length < 4 || (bytes[0] & 255) != 208 || (bytes[1] & 255) != 207))
            throw erro(BAD_REQUEST, "Documento inválido.");
        if (Set.of("txt", "csv").contains(extensao)) {
            try { StandardCharsets.UTF_8.newDecoder().decode(java.nio.ByteBuffer.wrap(bytes)); }
            catch (java.nio.charset.CharacterCodingException e) { throw erro(BAD_REQUEST, "Use um arquivo de texto UTF-8."); }
        }
        return extensao.equals("pdf") ? "application/pdf" : "application/octet-stream";
    }
    private boolean inicia(byte[] bytes, String assinatura) {
        return new String(bytes, 0, Math.min(bytes.length, assinatura.length()), StandardCharsets.ISO_8859_1).equals(assinatura);
    }
}
