package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Role;

public record UsuarioResponseDTO(
        Long id,
        String email
) {
}
