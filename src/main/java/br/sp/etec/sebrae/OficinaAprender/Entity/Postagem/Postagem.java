package br.sp.etec.sebrae.OficinaAprender.Entity.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoPostagem;
import br.sp.etec.sebrae.OficinaAprender.Enuns.Visualizacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Postagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "idMeta",
            foreignKey = @ForeignKey(name = "fk_meta")
    )
    Meta meta;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "responde_a_id")
    private Postagem respondeA;
    private String titulo;


    @Column(columnDefinition = "TEXT")
    private String conteudoHtml;

    @Enumerated(EnumType.STRING)
    private TipoPostagem tipoPostagem;

    @Enumerated(EnumType.STRING)
    private Visualizacao visualizacao;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}

