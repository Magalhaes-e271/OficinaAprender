package br.sp.etec.sebrae.OficinaAprender.Entity.Relation;

import br.sp.etec.sebrae.OficinaAprender.Entity.Aluno;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Role;
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
@Table(name = "usuario_aluno")
public class UsuarioAluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idaluno")
    private Aluno aluno;

    @Enumerated(EnumType.STRING)
    private Role role;

    private enum TipoAcesso {
        COMUM,
        ADMIN
    }

    private String foto;

}

