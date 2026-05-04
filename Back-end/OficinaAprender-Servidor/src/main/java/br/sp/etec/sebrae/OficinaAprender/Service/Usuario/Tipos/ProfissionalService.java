package br.sp.etec.sebrae.OficinaAprender.Service.Usuario.Tipos;

import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.Tipos.ProfissionalRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Profissional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }
    public Profissional salvar(Profissional profissional){
     return profissionalRepository.save(profissional);
    }
    // procurar por ID
    public Optional<Profissional> findById(Long id) {
        return profissionalRepository.findById(id);
    }



}
