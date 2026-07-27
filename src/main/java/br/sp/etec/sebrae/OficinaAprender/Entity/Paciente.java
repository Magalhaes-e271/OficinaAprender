package br.sp.etec.sebrae.OficinaAprender.Entity;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            cascade = CascadeType.ALL,
            optional = false
    )
    @JoinColumn(
            name = "id_descricao",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_descricao_paciente"
            )
    )
    private Descricao descricao;
}