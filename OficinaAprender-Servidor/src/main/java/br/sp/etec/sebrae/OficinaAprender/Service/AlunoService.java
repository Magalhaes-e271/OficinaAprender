package br.sp.etec.sebrae.OficinaAprender.Service;

import br.sp.etec.sebrae.OficinaAprender.Repository.AlunoRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Aluno;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AlunoService {
    private static AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    // procurar por ID
    public Optional<Aluno> findByID(Long id) {
        return alunoRepository.findById(id);
    }
    // Cadastrar Aluno
   public static Aluno salvar(Aluno aluno) {
       return alunoRepository.save(aluno);
    }
}



