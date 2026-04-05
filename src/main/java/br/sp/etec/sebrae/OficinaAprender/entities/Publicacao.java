package br.sp.etec.sebrae.OficinaAprender.entities;

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
public class Publicacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "id_profissional")
    private Profissional profissional;
    @ManyToOne
    @JoinColumn(name = "id_aluno")
    private Aluno aluno;
}
