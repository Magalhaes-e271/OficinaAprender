package br.sp.etec.sebrae.OficinaAprender.Controller.Postagem.anexo;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Anexo.AnexoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/anexo")
public class AnexoController {

    private final AnexoService anexoService;


    public AnexoController(
            AnexoService anexoService
    ) {

        this.anexoService =
                anexoService;
    }


    @PostMapping("/criar")
    public ResponseEntity<Anexo> criar(

            @RequestParam("arquivo")
            MultipartFile arquivo,

            @RequestParam("idPostagem")
            Long idPostagem,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return ResponseEntity.ok(

                anexoService.criar(

                        arquivo,

                        idPostagem,

                        authorizationHeader
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<Anexo> buscarPorId(

            @PathVariable Long id,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return ResponseEntity.ok(

                anexoService.findByID(
                        id,
                        authorizationHeader
                )
        );
    }


    @GetMapping("/postagem/{idPostagem}")
    public ResponseEntity<List<Anexo>> buscarPorPostagem(

            @PathVariable Long idPostagem,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return ResponseEntity.ok(

                anexoService.buscarPorPostagem(

                        idPostagem,

                        authorizationHeader
                )
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<Anexo> editar(

            @PathVariable Long id,

            @RequestBody Anexo anexo,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return ResponseEntity.ok(

                anexoService.editar(

                        id,

                        anexo,

                        authorizationHeader
                )
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(

            @PathVariable Long id,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        anexoService.deletar(
                id,
                authorizationHeader
        );


        return ResponseEntity
                .noContent()
                .build();
    }
}