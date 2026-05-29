package br.sp.etec.sebrae.OficinaAprender.Service.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao.PublicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class PostagemService {
    private static PublicacaoRepository publicacaoRepository;

    public static void setPublicacaoRepository(PublicacaoRepository publicacaoRepository) {
        PostagemService.publicacaoRepository = publicacaoRepository;
    }

    //procurar por ID
    public Optional<Postagem> findByID(Long id) { return publicacaoRepository.findById(id); }
    public static Postagem salvar(Postagem postagem) { return publicacaoRepository.save(postagem); }
}
