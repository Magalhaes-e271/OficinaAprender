package br.sp.etec.sebrae.OficinaAprender.Entity.Agenda;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Getter
@Setter
public class Compromisso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Usuario profissional;
    @ManyToOne
    private Usuario paciente;
    private String titulo;
    private Instant inicio;
    private Instant fim;
    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
