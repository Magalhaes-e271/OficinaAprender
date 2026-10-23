package br.sp.etec.sebrae.OficinaAprender.DTO.Login;

public record LoginRequestDTO(
        String email,
        String senha,
        String codigo
) {
    public LoginRequestDTO(String email, String senha) { this(email, senha, null); }
}
