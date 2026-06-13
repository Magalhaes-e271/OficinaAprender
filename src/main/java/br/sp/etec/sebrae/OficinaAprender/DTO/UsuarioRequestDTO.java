package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.res.Role;

public record UsuarioRequestDTO(
        String email,
        String senha,
        String nome,
        Role role
) {
}
