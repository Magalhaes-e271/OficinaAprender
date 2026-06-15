package br.sp.etec.sebrae.OficinaAprender.Service.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao.PostagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class PostagemService {
    private static PostagemRepository postagemRepository;

    public static void setPublicacaoRepository(PostagemRepository postagemRepository) {
        PostagemService.postagemRepository = postagemRepository;
    }

    //procurar por ID
    public Optional<Postagem> findByID(Long id) { return postagemRepository.findById(id); }
    public static Postagem salvar(Postagem postagem) { return postagemRepository.save(postagem);
        }
}
