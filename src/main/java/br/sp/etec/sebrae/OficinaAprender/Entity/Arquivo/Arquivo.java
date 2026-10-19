package br.sp.etec.sebrae.OficinaAprender.Entity.Arquivo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;

@Entity
@Getter
@Setter
public class Arquivo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String tipo;
    private long tamanho;
    private Instant dataCriacao = Instant.now();
    @JsonIgnore
    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] conteudo;
}
