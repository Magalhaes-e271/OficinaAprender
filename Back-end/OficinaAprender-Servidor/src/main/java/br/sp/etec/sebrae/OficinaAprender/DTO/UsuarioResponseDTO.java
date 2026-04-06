package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.entities.Usuario.Role;

public record UsuarioResponseDTO(
        Long id,
        String email,
        Role role
) {
}
