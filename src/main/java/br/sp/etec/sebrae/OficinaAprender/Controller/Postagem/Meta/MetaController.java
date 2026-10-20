package br.sp.etec.sebrae.OficinaAprender.Controller.Postagem.Meta;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta.MetaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/meta")
public class MetaController {

    private final MetaService metaService;


    public MetaController(
            MetaService metaService
    ) {

        this.metaService =
                metaService;
    }


    // CRIAR / SALVAR META
    @PostMapping("/criar")
    public ResponseEntity<?> criar(

            @RequestBody Meta meta,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return metaService.salvar(
                meta,
                authorizationHeader
        );
    }
}