package br.sp.etec.sebrae.OficinaAprender.Service.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import org.antlr.v4.runtime.misc.NotNull;
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
    public UsuarioResponseDTO findByEmail(String email){
        Usuario usr = usuarioRepository.findByEmail(email).orElse(null);
        return new  UsuarioResponseDTO( usr.getId(), usr.getEmail(), usr.getDescricao());}
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
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
