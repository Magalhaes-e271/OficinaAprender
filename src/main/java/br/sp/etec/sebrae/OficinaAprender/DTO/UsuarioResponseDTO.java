package br.sp.etec.sebrae.OficinaAprender.DTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;
public record UsuarioResponseDTO(Long id, String email, TipoUsuario tipoUsuario, PerfilUsuario perfil,
                                 String token, Long criadoPorId, boolean acessoHabilitado) {
    public UsuarioResponseDTO(Long id, String email, TipoUsuario tipoUsuario, PerfilUsuario perfil, String token) {
        this(id, email, tipoUsuario, perfil, token, null, true);
    }
    public UsuarioResponseDTO(Long id, String email, TipoUsuario tipoUsuario, PerfilUsuario perfil) {
        this(id, email, tipoUsuario, perfil, null, null, true);
    }
}
