package br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Relaction.ProfissionalAluno;
import br.sp.etec.sebrae.OficinaAprender.res.TipoPostagem;
import br.sp.etec.sebrae.OficinaAprender.res.VisibilidadePostagem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "publicacoes", schema = "ACONTEUDO")
public class Publicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @Enumerated(EnumType.STRING)
    private List<TipoPostagem> tipoPostagem;

    @Enumerated(EnumType.STRING)
    private List<VisibilidadePostagem> visibilidade;

    @ManyToOne
    @JoinColumn(name = "idProfissionalAluno")
    private ProfissionalAluno profissionalAluno;

    @OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anexo> anexos = new ArrayList<>();
}

