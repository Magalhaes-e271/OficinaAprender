package br.sp.etec.sebrae.OficinaAprender.Entity.Relation.Permissao;

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
public class PermissaoAcesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idSujeito;

    @Enumerated(EnumType.STRING)
    private permissao permissao;

    @JoinColumn(name = "idPostagem")
    private Long idPostagem;

    private enum permissao{
        Visualizar,
        Editar
    }


}
