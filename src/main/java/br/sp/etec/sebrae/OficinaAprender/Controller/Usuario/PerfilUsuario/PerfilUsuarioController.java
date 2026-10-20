package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario.PerfilUsuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.PerfilUsuario.PerfilUsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/perfil")
public class PerfilUsuarioController {

    private final PerfilUsuarioService perfilUsuarioService;

    public PerfilUsuarioController(
            PerfilUsuarioService perfilUsuarioService
    ) {
        this.perfilUsuarioService = perfilUsuarioService;
    }


    // BUSCAR PERFIL POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(

            @PathVariable Long id,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        var perfil =
                perfilUsuarioService.findByID(
                        id,
                        authorizationHeader
                );


        if (perfil.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Perfil não encontrado");
        }


        return ResponseEntity.ok(
                perfil.get()
        );
    }


    // CRIAR PERFIL
    @PostMapping("/criar")
    public ResponseEntity<?> criar(

            @RequestBody PerfilUsuario perfilUsuario,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        return perfilUsuarioService.criar(
                perfilUsuario,
                authorizationHeader
        );
    }


    // EDITAR PERFIL
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(

            @PathVariable Long id,

            @RequestBody PerfilUsuario perfilEditado,

            @RequestHeader(
                    value = "Authorization",
                    required = false
            )
            String authorizationHeader

    ) {

        var perfil =
                perfilUsuarioService.findByID(
                        id,
                        authorizationHeader
                );


        if (perfil.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Perfil não encontrado");
        }


        return ResponseEntity.ok(

                perfilUsuarioService.salvar(

                        id,

                        perfilEditado,

                        authorizationHeader
                )
        );
    }
}