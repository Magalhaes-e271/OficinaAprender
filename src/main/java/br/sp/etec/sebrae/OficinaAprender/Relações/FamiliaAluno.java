package br.sp.etec.sebrae.OficinaAprender.Relações;

import br.sp.etec.sebrae.OficinaAprender.entities.Aluno;

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
public class FamiliaAluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FamiliaAluno familiaAluno;
    @ManyToOne
    private Aluno aluno;
}