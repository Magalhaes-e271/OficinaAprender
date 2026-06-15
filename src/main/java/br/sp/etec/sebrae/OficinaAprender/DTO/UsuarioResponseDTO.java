package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;


public record UsuarioResponseDTO(
        Long id,
        String email,
        Descricao descricao
) {
    public Long getId() {
        return this.id;
    }


    public String getEmail() {
        return this.email;
    }

    public Descricao getDescricao() {
        return this.descricao;
    }
}
