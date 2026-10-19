package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario;

import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore
    private String senha;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    private boolean ativo = true;
    private boolean acessoHabilitado = true;
    @JsonIgnore
    private int versaoToken;
    @JsonIgnore
    private Long criadoPorId;



}
