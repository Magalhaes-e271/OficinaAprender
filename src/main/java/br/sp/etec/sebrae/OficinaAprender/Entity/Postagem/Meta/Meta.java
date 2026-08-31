package br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "idRelacaoUsuario",
            foreignKey = @ForeignKey(name = "fk_relacao_usuario")
    )
    private RelacaoUsuario relacaoUsuario;

    private String area;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String conteudoHtml;

    private boolean concluido;

    private Date dataCriacao;
    private Date dataAtualizacao;

}

