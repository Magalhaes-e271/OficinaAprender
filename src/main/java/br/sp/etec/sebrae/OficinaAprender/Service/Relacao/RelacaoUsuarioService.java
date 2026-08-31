package br.sp.etec.sebrae.OficinaAprender.Service.Relacao;

import br.sp.etec.sebrae.OficinaAprender.Repository.Relacao.RelacaoUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelacaoUsuarioService {

    private final RelacaoUsuarioRepository relacaoUsuarioRepository;

    public RelacaoUsuarioService(
            RelacaoUsuarioRepository relacaoUsuarioRepository
    ) {
        this.relacaoUsuarioRepository = relacaoUsuarioRepository;
    }

    public RelacaoUsuario salvar(RelacaoUsuario relacaoUsuario) {
        return relacaoUsuarioRepository.save(relacaoUsuario);
    }

    public Optional<RelacaoUsuario> findByID(long id){return relacaoUsuarioRepository.findById(id);}

    public ResponseEntity<?> criar(RelacaoUsuario relacaoUsuario){
        if (relacaoUsuario.getId() != null) {
            throw new RuntimeException(
                    "Uma nova relação não deve possuir ID"
            );
        }

        return ResponseEntity.ok(relacaoUsuarioRepository.save(relacaoUsuario));
    }

}
