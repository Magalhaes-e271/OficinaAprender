package br.sp.etec.sebrae.OficinaAprender.Service.Publicacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Publicacao;
import br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao.PublicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class PublicacaoService {
    private static PublicacaoRepository publicacaoRepository;

    public static void setPublicacaoRepository(PublicacaoRepository publicacaoRepository) {
        PublicacaoService.publicacaoRepository = publicacaoRepository;
    }

    //procurar por ID
    public Optional<Publicacao> findByID(Long id) { return publicacaoRepository.findById(id); }
    public static Publicacao salvar(Publicacao publicacao) { return publicacaoRepository.save(publicacao); }
}
