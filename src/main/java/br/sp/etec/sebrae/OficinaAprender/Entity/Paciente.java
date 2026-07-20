package br.sp.etec.sebrae.OficinaAprender.Entity;

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
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @JoinColumn(
            name = "descricao",
            foreignKey = @ForeignKey(name = "fk_descricao_paciente")
    )
    Long idDescricao;
}