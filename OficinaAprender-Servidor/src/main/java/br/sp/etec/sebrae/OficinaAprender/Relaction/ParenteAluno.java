package br.sp.etec.sebrae.OficinaAprender.Relaction;

import br.sp.etec.sebrae.OficinaAprender.Entity.Aluno;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Parente;
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
@Table(name = "parente_aluno", schema = "ARELACAO")
public class ParenteAluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idParente")
    private Parente parente;
    @ManyToOne
    @JoinColumn(name = "idAluno")
    private Aluno aluno;
}