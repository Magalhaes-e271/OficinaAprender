package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

}