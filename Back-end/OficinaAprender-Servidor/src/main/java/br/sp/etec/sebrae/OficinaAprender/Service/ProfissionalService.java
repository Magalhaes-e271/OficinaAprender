package br.sp.etec.sebrae.OficinaAprender.Service;

import br.sp.etec.sebrae.OficinaAprender.Repository.AlunoRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.ProfissionalRepository;
import br.sp.etec.sebrae.OficinaAprender.entities.Profissional;
import br.sp.etec.sebrae.OficinaAprender.entities.Usuario.Usuario;
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
//    // Criar Profissional
////    public Profissional criarProfissional(ProfissionalDTO pro) {
////        Usuario usuario = new Usuario();
////        usuario.setEmail(pro.get);
//
//    return profissionalRepository.save(Pro);
//    }


}
