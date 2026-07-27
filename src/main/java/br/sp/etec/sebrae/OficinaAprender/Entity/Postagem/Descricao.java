package br.sp.etec.sebrae.OficinaAprender.Entity.Postagem;

import br.sp.etec.sebrae.OficinaAprender.res.TipoDescricao;
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
public class Descricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String resumo;
    private String fotoUrl;

    @Column(columnDefinition = "TEXT")
    private String conteudoHtml;

    private String telefone;
    private String instituicao;
    private String formacao;
    private String email;

    @Enumerated(EnumType.STRING)
    private TipoDescricao tipoDescricao;
}