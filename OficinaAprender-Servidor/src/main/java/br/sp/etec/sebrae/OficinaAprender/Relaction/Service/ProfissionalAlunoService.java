package br.sp.etec.sebrae.OficinaAprender.Relaction.Service;

import br.sp.etec.sebrae.OficinaAprender.Relaction.ProfissionalAluno;
import br.sp.etec.sebrae.OficinaAprender.Relaction.Repository.ProfissionalAlunoRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProfissionalAlunoService {
    private static ProfissionalAlunoRepository profissionalAlunoRepository;

    public Optional<ProfissionalAluno> findByID(long id){return profissionalAlunoRepository.findById(id);};

    public ProfissionalAluno criar(ProfissionalAluno pa){return profissionalAlunoRepository.save(pa);}

}
