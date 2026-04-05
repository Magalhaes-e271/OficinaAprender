package br.sp.etec.sebrae.OficinaAprender.Relações;

import br.sp.etec.sebrae.OficinaAprender.entities.Aluno;
import br.sp.etec.sebrae.OficinaAprender.entities.Profissional;
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
public class Profissional_Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    private Profissional profissional;
    @ManyToOne
    private Aluno aluno;
}
