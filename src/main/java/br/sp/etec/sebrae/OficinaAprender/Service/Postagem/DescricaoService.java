package br.sp.etec.sebrae.OficinaAprender.Service.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;
import br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao.DescricaoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DescricaoService {
    private static DescricaoRepository descricaoRepository;
    public static void setDescricaoRepository(DescricaoRepository descricaoRepository) {
        DescricaoService.descricaoRepository = descricaoRepository;
    }
    public Optional<Descricao> findByID(Long id) { return descricaoRepository.findById(id); }
    public static Descricao salvar(Descricao descricao) { return descricaoRepository.save(descricao); }
}