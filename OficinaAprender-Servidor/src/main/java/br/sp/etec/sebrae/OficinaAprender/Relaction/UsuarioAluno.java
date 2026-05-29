package br.sp.etec.sebrae.OficinaAprender.Relaction;

import br.sp.etec.sebrae.OficinaAprender.Entity.Aluno;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Role;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.message.StringFormattedMessage;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario_aluno", schema = "ARELACAO")
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

    private Role role;


}

