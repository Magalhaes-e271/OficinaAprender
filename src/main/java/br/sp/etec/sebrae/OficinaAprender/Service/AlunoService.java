package br.sp.etec.sebrae.OficinaAprender.Service;

import br.sp.etec.sebrae.OficinaAprender.Repository.AlunoRepository;
import br.sp.etec.sebrae.OficinaAprender.entities.Aluno;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AlunoService {
    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    // procurar por ID
    public Optional<Aluno> AcharPorID(Long id) {
        return alunoRepository.findById(id);
    }
    // Cadastrar Aluno
   public Aluno CadastraAluno(Aluno aluno) {
        return alunoRepository.save(aluno);
   }


}
