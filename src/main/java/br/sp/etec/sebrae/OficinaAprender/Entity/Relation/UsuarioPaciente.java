package br.sp.etec.sebrae.OficinaAprender.Entity.Relation;

import br.sp.etec.sebrae.OficinaAprender.Entity.Paciente;
import br.sp.etec.sebrae.OficinaAprender.res.Role;
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
@Table(name = "usuario_paciente")
public class UsuarioPaciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(
            name = "idUsuario",
            foreignKey = @ForeignKey(name = "fk_usuario")
    )
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(
            name = "idPaciente",
            foreignKey = @ForeignKey(name = "fk_paciente")
    )
    private Paciente Paciente;

    @Enumerated(EnumType.STRING)
    private Role role;

    private enum TipoAcesso {
        COMUM,
        ADMIN
    }

    private String foto;

}

