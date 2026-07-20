package br.sp.etec.sebrae.OficinaAprender.Entity.Relation.Permissao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.UsuarioPaciente;
import br.sp.etec.sebrae.OficinaAprender.res.Permissao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.mapping.UnionSubclass;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissaoAcesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Permissao permissao;

    @ManyToOne
    @JoinColumn(
            name = "idPostagem",
            foreignKey = @ForeignKey(name = "fk_postagem")
    )
    Postagem postagem;
    @ManyToOne
    @JoinColumn(
            name = "idUsuarioPaciente",
            foreignKey = @ForeignKey(name = "fk_UsuarioPaciente")
    )
    UsuarioPaciente usuarioPaciente;

}
