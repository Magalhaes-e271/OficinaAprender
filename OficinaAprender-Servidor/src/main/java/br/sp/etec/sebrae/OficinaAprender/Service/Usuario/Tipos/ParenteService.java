package br.sp.etec.sebrae.OficinaAprender.Service.Usuario.Tipos;


import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.Tipos.ParenteRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Parente;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
public class ParenteService {

    public final ParenteRepository parenteRepository;

    public ParenteService(ParenteRepository parenteRepository) {
        this.parenteRepository = parenteRepository;
    }

/// Achar por Id
    public Optional<Parente> findById(Long id) {
        return parenteRepository.findById(id);
    }

/// Cadastrar
    public Parente salvar(Parente parente) {

        return parenteRepository.save(parente);
    }


}

