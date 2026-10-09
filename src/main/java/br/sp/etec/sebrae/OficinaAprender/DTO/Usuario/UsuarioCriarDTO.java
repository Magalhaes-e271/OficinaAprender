package br.sp.etec.sebrae.OficinaAprender.DTO.Usuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;

public record UsuarioCriarDTO(
        String email,
        String senha,
        TipoUsuario tipoUsuario,
        PerfilUsuario perfilUsuario
) {

}
