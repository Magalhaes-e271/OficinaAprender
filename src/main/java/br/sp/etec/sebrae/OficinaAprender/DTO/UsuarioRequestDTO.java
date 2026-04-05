package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.entities.Usuario.Role;

public record UsuarioRequestDTO(
        String email,
        String senha,
        Role role
) {
}
