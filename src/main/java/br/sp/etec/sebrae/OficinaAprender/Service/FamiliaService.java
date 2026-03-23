package br.sp.etec.sebrae.OficinaAprender.Service;


import br.sp.etec.sebrae.OficinaAprender.Repository.FamiliaRepository;
import br.sp.etec.sebrae.OficinaAprender.entities.PessoaFamilia;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
public class FamiliaService {

    public final FamiliaRepository familiaRepository;

    public FamiliaService(FamiliaRepository familiaRepository) {
        this.familiaRepository = familiaRepository;
    }

/// Achar por Id
    public Optional<PessoaFamilia> findById(Long id) {
        return familiaRepository.findById(id);
    }

/// Cadastrar
    public PessoaFamilia CadastrarFamilia(PessoaFamilia pessoaFamilia) {
        return familiaRepository.save(pessoaFamilia);
    }


}

