package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonIgnore
    private Long fotoId;

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
    @Column(length = 2000)
    @JsonAlias("condicoes")
    private String condicao;

    @JsonProperty("condicoes")
    public String getCondicoes() { return condicao; }

    @JsonProperty("condicoes")
    public void setCondicoes(String condicoes) { this.condicao = condicoes; }

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
