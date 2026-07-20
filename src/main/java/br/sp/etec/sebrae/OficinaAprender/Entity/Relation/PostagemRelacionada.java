package br.sp.etec.sebrae.OficinaAprender.Entity.Relation;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
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
public class PostagemRelacionada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "idPostagemMae",
            foreignKey = @ForeignKey(name = "fk_postagem_Mae")
    )
    private Postagem idPostagemMae;

    @ManyToOne
    @JoinColumn(
            name = "idPostagemFilha",
            foreignKey = @ForeignKey(name = "fk_postagem_Filha")
    )
    private Postagem idPostagemFilha;
}
