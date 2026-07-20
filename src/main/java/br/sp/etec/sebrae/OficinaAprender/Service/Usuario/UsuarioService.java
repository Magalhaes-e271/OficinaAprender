package br.sp.etec.sebrae.OficinaAprender.Service.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO Criar(UsuarioRequestDTO dto) {

        boolean emailJaExiste = usuarioRepository.findByEmail(dto.email()).isPresent();

        if (emailJaExiste) {
            throw new RuntimeException("Já existe um usuário com esse email");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getDescricao()
        );
    }
    public ResponseEntity<?> findByEmail(String email){
        Usuario usr = usuarioRepository.findByEmail(email).orElse(null);
        if(usr == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario nao encotrado");
        }
        return ResponseEntity.ok(new UsuarioResponseDTO( usr.getId(), usr.getEmail(), usr.getDescricao()));}

    public ResponseEntity<?> findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if(usuario == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Não foi possivel encontra um usuario com esse id");
        }
        return ResponseEntity.ok(usuario);
    }

    public Usuario editar(Long id, Usuario dadosAtualizados) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuarioExistente.setEmail(dadosAtualizados.getEmail());
        usuarioExistente.setSenha(dadosAtualizados.getSenha());

        return usuarioRepository.save(usuarioExistente);
    }
    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }
}
