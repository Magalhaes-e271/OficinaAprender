package br.sp.etec.sebrae.OficinaAprender.Entity.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Relaction.Permissao.PermissaoAcesso;
import br.sp.etec.sebrae.OficinaAprender.Relaction.UsuarioAluno;
import br.sp.etec.sebrae.OficinaAprender.res.TipoPostagem;
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
public class Postagem {

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

    @OneToMany(mappedBy = "IDAlvo")
    private List<PermissaoAcesso> permissoes = new ArrayList<>() ;

    @ManyToOne
    @JoinColumn(name = "idUsuarioAluno")
    private UsuarioAluno usuarioAluno;

    @OneToMany(mappedBy = "postagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anexo> anexos = new ArrayList<>();
}

