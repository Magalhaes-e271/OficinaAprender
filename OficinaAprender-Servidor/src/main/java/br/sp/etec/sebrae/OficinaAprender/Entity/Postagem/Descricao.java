package br.sp.etec.sebrae.OficinaAprender.Entity.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Aluno;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
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
    private long id;

    private String nome;
    private String resumo;
    private String Fotourl;

    @Column(columnDefinition = "TEXT")
    private String conteudoHtml;

    private String telefone;
    private String instituicao;
    private String formacao;

    private enum TipoDescricao{
        ALUNO,
        USUARIO
    }
    @OneToOne(mappedBy = "descricao", cascade = CascadeType.ALL)
    @JoinColumn(name = "idAluno")
    private Aluno aluno;

    @OneToOne(mappedBy = "descricao", cascade = CascadeType.ALL)
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
}
