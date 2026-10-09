package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilUsuario {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JsonIgnore
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String nome;
    private String telefone;
    private String cpf;
    private String urlFotoPerfil;

    // Campo que faltava: usado pelo service para localizar a foto no disco.
    // O Lombok gera getFotoArquivo() e setFotoArquivo().
    @JsonIgnore
    @Column(name = "foto_arquivo")
    private String fotoArquivo;

    private Date dataNascimento;
    private String ocupacao;
    private String instituicao;

    @Column(columnDefinition = "TEXT")
    private String conteudoHtml;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    private LocalTime horarioDeEntrada;
    private LocalTime horarioDeSaida;
    private String condicao;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();
        dataCriacao = agora;
        dataAtualizacao = agora;
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}