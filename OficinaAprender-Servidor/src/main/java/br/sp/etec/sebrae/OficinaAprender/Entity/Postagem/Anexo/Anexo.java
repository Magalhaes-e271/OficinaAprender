package br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Anexo;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
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
    @JoinColumn(name = "Postagem_id")
    private Postagem postagem;
}
