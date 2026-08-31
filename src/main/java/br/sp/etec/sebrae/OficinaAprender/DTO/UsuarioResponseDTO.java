package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;


public record UsuarioResponseDTO(
        Long id,
        String email,
        TipoUsuario tipoUsuario,
        PerfilUsuario perfil
)
{
    @Override
    public Long id() {
        return id;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public TipoUsuario tipoUsuario() {
        return tipoUsuario;
    }

    @Override
    public PerfilUsuario perfil() {
        return perfil;
    }
}
