package br.sp.etec.sebrae.OficinaAprender.Relaction;

import br.sp.etec.sebrae.OficinaAprender.Entity.Aluno;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Profissional;
import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Publicacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ProfissionalAluno", schema = "ARELACAO")
public class ProfissionalAluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "idProfissional")
    private Profissional profissional;
    @ManyToOne
    @JoinColumn(name = "idAluno")
    private Aluno aluno;

    @OneToMany(mappedBy = "profissionalAluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publicacao> publicacoes = new ArrayList<>();
}
