package br.sp.etec.sebrae.OficinaAprender.Controller.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.PostagemService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/postagem")
public class PostagemController {

    private final PostagemService postagemService;


    public PostagemController(
            PostagemService postagemService
    ) {

        this.postagemService =
                postagemService;
    }


    @PostMapping("/criar")
    public ResponseEntity<?> criar(

            @RequestBody Postagem postagem,

            @RequestParam Long idMeta,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return ResponseEntity.ok(

                postagemService.criar(

                        postagem,

                        idMeta,

                        authorizationHeader
                )
        );
    }
}