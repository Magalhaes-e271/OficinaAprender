package br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Anexo;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Anexo.AnexoRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.PostagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class AnexoService {

    private final AnexoRepository anexoRepository;
    private final PostagemRepository postagemRepository;

    public AnexoService(
            AnexoRepository anexoRepository,
            PostagemRepository postagemRepository
    ) {
        this.anexoRepository = anexoRepository;
        this.postagemRepository = postagemRepository;
    }


    private final Path pastaArquivos = Paths.get("uploads");
    public Anexo criar(
            MultipartFile arquivo,
            Long idPostagem
    ) {

        Postagem postagem = postagemRepository.findById(idPostagem)
                .orElseThrow(() ->
                        new RuntimeException("Postagem não encontrada")
                );

        if (arquivo.isEmpty()) {
            throw new RuntimeException("Arquivo vazio");
        }

        try {

            Files.createDirectories(pastaArquivos);

            String nomeOriginal = arquivo.getOriginalFilename();

            String nomeArquivo = UUID.randomUUID() + "_" + nomeOriginal;

            Path caminho = pastaArquivos.resolve(nomeArquivo);

            Files.copy(
                    arquivo.getInputStream(),
                    caminho
            );

            Anexo anexo = new Anexo();

            anexo.setPostagem(postagem);

            anexo.setNome(nomeOriginal);

            anexo.setUrlArquivo(
                    caminho.toString()
            );

            anexo.setTamanhoArquivo(
                    arquivo.getSize()
            );

            anexo.setTipoArquivo(
                    arquivo.getContentType()
            );

            anexo.setDataUpload(
                    LocalDateTime.now()
            );

            return anexoRepository.save(anexo);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao salvar arquivo",
                    e
            );
        }
    }



    public Anexo findByID(Long id) {
        return anexoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Anexo não encontrado")
                );
    }

    public List<Anexo> buscarPorPostagem(Long idPostagem) {

        if (!postagemRepository.existsById(idPostagem)) {
            throw new RuntimeException("Postagem não encontrada");
        }

        return anexoRepository.findByPostagemId(idPostagem);
    }

    public Anexo editar(Long id, Anexo dados) {

        Anexo anexo = findByID(id);

        if (dados.getNome() != null) {
            anexo.setNome(dados.getNome());
        }

        if (dados.getUrlArquivo() != null) {
            anexo.setUrlArquivo(dados.getUrlArquivo());
        }

        if (dados.getTamanhoArquivo() != null) {
            anexo.setTamanhoArquivo(dados.getTamanhoArquivo());
        }

        if (dados.getTipoArquivo() != null) {
            anexo.setTipoArquivo(dados.getTipoArquivo());
        }

        return anexoRepository.save(anexo);
    }

    public void deletar(Long id) {

        Anexo anexo = findByID(id);

        anexoRepository.delete(anexo);
    }
}