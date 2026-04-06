package br.sp.etec.sebrae.OficinaAprender.entities;

import br.sp.etec.sebrae.OficinaAprender.entities.Usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Parente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;

    @OneToOne
    private Usuario usuario;

}
