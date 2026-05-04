package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios", schema = "ASEGURANCA")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    private Role role;
}