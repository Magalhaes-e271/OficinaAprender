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
public class Parente_Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Parente_Aluno parenteAluno;
    @ManyToOne
    private Aluno aluno;
}