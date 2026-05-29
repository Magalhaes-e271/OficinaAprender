package br.sp.etec.sebrae.OficinaAprender.Relaction.Permissao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissaoAcesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String IDSujeito;

    @Enumerated(EnumType.STRING)
    private TipoSujeito tipoSujeito;

    @Enumerated(EnumType.STRING)
    private permissao permissao;

    @Enumerated(EnumType.STRING)
    private TipoAlvo tipoAlvo;


    @JoinColumn(name = "IDAlvo")
    private Long IDAlvo;

    private enum TipoSujeito{
        PARENTE,
        PROFISSIONAL,
        USUARIO,
        ESCOLA
    }
    private enum TipoAlvo{
        POSTAGEM,
        ALUNO
    }

    private enum permissao{
        Visualizar,
        Editar
    }


}
