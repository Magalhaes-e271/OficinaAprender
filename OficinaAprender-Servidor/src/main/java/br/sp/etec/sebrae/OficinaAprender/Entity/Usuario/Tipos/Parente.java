package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
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
@Table(name = "parente", schema = "APERFIL")
public class Parente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;

    @OneToOne
    private Usuario usuario;

}
