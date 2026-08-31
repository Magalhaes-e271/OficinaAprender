package br.sp.etec.sebrae.OficinaAprender.Service.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Meta.MetaRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.PostagemRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta.MetaService;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class PostagemService {
    private final PostagemRepository postagemRepository;
    private final MetaRepository metaRepository;
    public PostagemService(PostagemRepository postagemRepository, MetaRepository meta) {
        this.postagemRepository = postagemRepository;
        this.metaRepository = meta;
    }

    //procurar por ID
    public Optional<Postagem> findByID(Long id) {
        return postagemRepository.findById(id);
    }

    public Postagem criar(
            Postagem postagem,
            Long idMeta) {
        Meta meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new RuntimeException("Meta não encontrada"));
        try{

            return postagemRepository.save(postagem);
        } catch (Exception e){
            throw new RuntimeException(
                "Erro ao salvar postagem",
                e
        );
        }
    }

    ;
}

