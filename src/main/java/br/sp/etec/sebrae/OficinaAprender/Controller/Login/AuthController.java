package br.sp.etec.sebrae.OficinaAprender.Controller.Login;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    private final UsuarioService usuarioService;

    private final TokenService tokenService;


    public AuthController(

            UsuarioRepository usuarioRepository,

            UsuarioService usuarioService,

            TokenService tokenService

    ) {

        this.usuarioRepository =
                usuarioRepository;

        this.usuarioService =
                usuarioService;

        this.tokenService =
                tokenService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody UsuarioRequestDTO dados
    ) {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(
                                dados.email()
                        )
                        .orElse(null);


        if (usuario == null) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            "Email ou senha inválidos."
                    );
        }


        if (
                !usuario
                        .getSenha()
                        .equals(
                                dados.senha()
                        )
        ) {

            return ResponseEntity
                    .status(
                            HttpStatus.UNAUTHORIZED
                    )
                    .body(
                            "Email ou senha inválidos."
                    );
        }


        String token =
                tokenService
                        .gerarToken(usuario);


        UsuarioResponseDTO resposta =
                usuarioService
                        .converterParaDTO(
                                usuario,
                                token
                        );


        return ResponseEntity.ok(
                resposta
        );
    }
}