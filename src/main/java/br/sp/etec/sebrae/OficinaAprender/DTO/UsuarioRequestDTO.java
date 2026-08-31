package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;

public record UsuarioRequestDTO(
        String email,
        String senha,
        TipoUsuario tipoUsuario,
        PerfilUsuario perfilUsuario
) {
}
