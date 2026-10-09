package br.sp.etec.sebrae.OficinaAprender.Controller.Relacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Relacao.RelacaoUsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario-aluno")
public class UsuarioRelacaoController {

    private final RelacaoUsuarioService relacaoUsuarioService;


    public UsuarioRelacaoController(

            RelacaoUsuarioService relacaoUsuarioService

    ) {

        this.relacaoUsuarioService =
                relacaoUsuarioService;
    }


    @PostMapping("/criar")
    public ResponseEntity<?> criar(

            @RequestBody
            RelacaoUsuario relacaoUsuario,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return relacaoUsuarioService.criar(

                relacaoUsuario,

                authorizationHeader
        );
    }


    @PutMapping("/editar")
    public ResponseEntity<?> editar(

            @RequestBody
            RelacaoUsuario relacaoUsuario,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        relacaoUsuarioService.salvar(

                relacaoUsuario,

                authorizationHeader
        );


        return ResponseEntity
                .ok()
                .build();
    }
}