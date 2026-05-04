package br.sp.etec.sebrae.OficinaAprender.Service.Publicacao.Anexo;

import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao.Anexo.AnexoRepository;

import java.util.Optional;

public class AnexoService {
    private static AnexoRepository anexoRepository;

    public AnexoService(AnexoRepository anexoRepository) { AnexoService.anexoRepository = anexoRepository;}



    //procurar por ID
    public Optional<Anexo> findByID(Long id) { return anexoRepository.findById(id); }
    //  salvar anexo
    public static Anexo salvar(Anexo anexo) { return anexoRepository.save(anexo); }

}

