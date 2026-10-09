package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;

public record UsuarioRequestDTO(
        Long id,
        String email,
        String senha,
        TipoUsuario tipoUsuario,
        String token


) {
}
