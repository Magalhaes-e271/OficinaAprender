package br.sp.etec.sebrae.OficinaAprender.Service.Relation;

import br.sp.etec.sebrae.OficinaAprender.Repository.Relation.UsuarioAlunoRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.UsuarioPaciente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioPacienteService {

    private final UsuarioAlunoRepository usuarioAlunoRepository;

    public UsuarioPacienteService(
            UsuarioAlunoRepository usuarioAlunoRepository
    ) {
        this.usuarioAlunoRepository = usuarioAlunoRepository;
    }

    public UsuarioPaciente salvar(UsuarioPaciente usuarioPaciente) {
        return usuarioAlunoRepository.save(usuarioPaciente);
    }

    public Optional<UsuarioPaciente> findByID(long id){return usuarioAlunoRepository.findById(id);}

    public ResponseEntity<?> criar(UsuarioPaciente usuarioPaciente){
        if (usuarioPaciente.getId() == 0) {
            throw new RuntimeException(
                    "Uma nova relação não deve possuir ID"
            );
        }

        return ResponseEntity.ok(usuarioAlunoRepository.save(usuarioPaciente));
    }

}
