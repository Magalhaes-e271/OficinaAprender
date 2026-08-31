package br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;

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
    private Date dataNascimento;
    private String ocupacao;
    private String instituicao;

    @Column(columnDefinition = "TEXT")
    private String conteudoHtml;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}