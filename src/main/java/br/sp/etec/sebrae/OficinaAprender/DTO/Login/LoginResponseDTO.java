package br.sp.etec.sebrae.OficinaAprender.DTO.Login;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;

public record LoginResponseDTO(
        String token,
        UsuarioResponseDTO usuario
) {
}