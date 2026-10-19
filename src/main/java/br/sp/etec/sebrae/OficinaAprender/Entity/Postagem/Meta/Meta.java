package br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.LinkedHashSet;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;


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

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Usuario paciente;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Usuario responsavel;

    @ManyToMany
    @JoinTable(name = "meta_dependencia", joinColumns = @JoinColumn(name = "meta_id"),
            inverseJoinColumns = @JoinColumn(name = "anterior_id"))
    private Set<Meta> anteriores = new LinkedHashSet<>();

    private String area;
    private String titulo;
    @Column(columnDefinition = "TEXT")
    private String conteudoHtml;

    private boolean concluido;

    private Date dataCriacao;
    private Date dataAtualizacao;

}

