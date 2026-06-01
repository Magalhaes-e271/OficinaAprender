package br.sp.etec.sebrae.OficinaAprender.Service.Relation;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.PostagemRelacionada;
import br.sp.etec.sebrae.OficinaAprender.Repository.Relation.PostagemRelacionadaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostagensRelaciondasService {
    private static PostagemRelacionadaRepository postagemRelacionadaRepository;

    public static Optional<PostagemRelacionada> findByID(Long id){return postagemRelacionadaRepository.findById(id);}

    public static PostagemRelacionada salvar(PostagemRelacionada pr){return postagemRelacionadaRepository.save(pr);}

}
