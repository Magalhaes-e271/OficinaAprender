package br.sp.etec.sebrae.OficinaAprender.Service;


import br.sp.etec.sebrae.OficinaAprender.Repository.ParenteRepository;
import br.sp.etec.sebrae.OficinaAprender.entities.Parente;
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
    public Parente CadastrarFamilia(Parente parente) {
        return parenteRepository.save(parente);
    }


}

