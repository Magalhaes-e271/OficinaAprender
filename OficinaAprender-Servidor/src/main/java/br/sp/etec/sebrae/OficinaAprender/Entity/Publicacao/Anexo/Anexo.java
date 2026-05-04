package br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Anexo;

import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Publicacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "anexo", schema = "PUBLICACAO")
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeOriginal;

    private String nomeArquivoSalvo;

    private String tipoArquivo;

    private String urlArquivo;

    private Long tamanhoArquivo;

    private LocalDateTime dataUpload;

    @ManyToOne
    @JoinColumn(name = "publicacao_id")
    private Publicacao publicacao;
}
