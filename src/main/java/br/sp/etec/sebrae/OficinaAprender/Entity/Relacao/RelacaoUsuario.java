package br.sp.etec.sebrae.OficinaAprender.Entity.Relacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.Solicitacao;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoRelacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelacaoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "idUsuarioSolicitante",
            foreignKey = @ForeignKey(name = "fk_usuario")
    )
    private Usuario usuarioSolicitante;

    @ManyToOne
    @JoinColumn(
            name = "idUsuarioDestinatario",
            foreignKey = @ForeignKey(name = "fk_usuario")
    )
    private Usuario usuarioDestinatario;

    @Enumerated(EnumType.STRING)
    private TipoRelacao TipoRelacao;
    private Solicitacao solicitacao;
    private Date dataSolicitacao;
    private Date dataAceitacao;

}

