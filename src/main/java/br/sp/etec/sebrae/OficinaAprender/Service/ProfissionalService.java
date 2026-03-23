package br.sp.etec.sebrae.OficinaAprender.Service;

import br.sp.etec.sebrae.OficinaAprender.Repository.AlunoRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.ProfissionalRepository;
import br.sp.etec.sebrae.OficinaAprender.entities.Profissional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalService(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    // procurar por ID
    public Optional<Profissional> findById(Long id) {
        return profissionalRepository.findById(id);
    }
    // Cadastrar Profissional
    public Profissional cadastroProfissional(Profissional Pro) {
       return profissionalRepository.save(Pro);
    }


}
