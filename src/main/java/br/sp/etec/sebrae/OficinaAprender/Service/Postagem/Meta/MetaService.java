package br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Meta.MetaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MetaService {
    private final MetaRepository metaRepository;
    public MetaService(MetaRepository metaRepository){
     this.metaRepository = metaRepository;
    }
 public ResponseEntity<?> salvar(Meta meta){
     return ResponseEntity.ok(metaRepository.save(meta));
 }
}
