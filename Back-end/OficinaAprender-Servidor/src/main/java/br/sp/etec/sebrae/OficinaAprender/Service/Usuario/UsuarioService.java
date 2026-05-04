package br.sp.etec.sebrae.OficinaAprender.Service.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO salvar(UsuarioRequestDTO dto) {

        boolean emailJaExiste = usuarioRepository.findByEmail(dto.email()).isPresent();

        if (emailJaExiste) {
            throw new RuntimeException("Já existe um usuário com esse email");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setRole(dto.role());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getRole()
        );
    }
}
