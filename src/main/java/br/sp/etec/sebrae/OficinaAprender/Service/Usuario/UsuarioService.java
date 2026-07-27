package br.sp.etec.sebrae.OficinaAprender.Service.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {

        boolean emailJaExiste =
                usuarioRepository.findByEmail(dto.email()).isPresent();

        if (emailJaExiste) {
            throw new RuntimeException(
                    "Já existe um usuário com esse e-mail"
            );
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaDTO(usuarioSalvo);
    }

    public UsuarioResponseDTO findByEmail(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado")
                );

        return converterParaDTO(usuario);
    }

    public Usuario findById(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Não foi possível encontrar um usuário com esse ID"
                        )
                );
    }

    public Usuario editar(Long id, Usuario dadosAtualizados) {

        Usuario usuarioExistente = findById(id);

        usuarioExistente.setEmail(dadosAtualizados.getEmail());
        usuarioExistente.setSenha(dadosAtualizados.getSenha());

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletar(Long id) {

        Usuario usuario = findById(id);

        usuarioRepository.delete(usuario);
    }

    private UsuarioResponseDTO converterParaDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getDescricao()
        );
    }
}