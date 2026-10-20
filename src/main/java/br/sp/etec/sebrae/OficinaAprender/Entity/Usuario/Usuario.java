package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario;

import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;



}