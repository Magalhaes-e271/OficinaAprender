package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.Usuario.UsuarioCriarDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.PerfilUsuario.PerfilUsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PerfilUsuarioService perfilUsuarioService;

    public UsuarioController(
            UsuarioService usuarioService,
            PerfilUsuarioService perfilUsuarioService
    ) {
        this.usuarioService = usuarioService;
        this.perfilUsuarioService = perfilUsuarioService;
    }

    // Cadastro público: recebe os dados em JSON e a foto no mesmo multipart.
    @PostMapping(
            value = "/criar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponseDTO> criarConta(
            @RequestPart("dados") UsuarioCriarDTO dto,
            @RequestPart("foto") MultipartFile foto
    ) {
        if (foto.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Envie uma foto para concluir o cadastro."
            );
        }

        LoginResponseDTO resposta = usuarioService.criar(dto, foto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    @GetMapping(
            value = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UsuarioResponseDTO> buscarSessao(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        return ResponseEntity.ok(
                usuarioService.buscarSessao(authorizationHeader)
        );
    }

    // Leitura da foto: exige token.
    @GetMapping("/foto")
    public ResponseEntity<Resource> buscarFoto(
            @RequestParam(name = "id") Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        Resource foto = usuarioService.buscarFoto(
                id,
                authorizationHeader
        );

        String nomeArquivo = foto.getFilename();
        MediaType tipoImagem;

        if (nomeArquivo != null && nomeArquivo.endsWith(".png")) {
            tipoImagem = MediaType.IMAGE_PNG;
        } else if (nomeArquivo != null && nomeArquivo.endsWith(".gif")) {
            tipoImagem = MediaType.IMAGE_GIF;
        } else {
            tipoImagem = MediaType.IMAGE_JPEG;
        }

        return ResponseEntity.ok()
                .contentType(tipoImagem)
                .cacheControl(CacheControl.noStore())
                .header("X-Content-Type-Options", "nosniff")
                .body(foto);
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editarConta(
            @RequestParam(name = "id") Long id,
            @RequestBody Usuario usuario,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        return ResponseEntity.ok(
                usuarioService.editar(
                        id,
                        usuario,
                        authorizationHeader
                )
        );
    }

    @DeleteMapping("/deletar")
    public ResponseEntity<Void> deletarConta(
            @RequestParam(name = "id") Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        usuarioService.deletar(id, authorizationHeader);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email")
    public ResponseEntity<?> buscarPorEmail(
            @RequestParam(name = "email") String email,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        return ResponseEntity.ok(
                usuarioService.findByEmail(
                        email,
                        authorizationHeader
                )
        );
    }

    @GetMapping("/id")
    public ResponseEntity<?> buscarPorId(
            @RequestParam(name = "id") Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        return ResponseEntity.ok(
                usuarioService.findById(
                        id,
                        authorizationHeader
                )
        );
    }

    @GetMapping("/descricao")
    public ResponseEntity<?> buscarDescricao(
            @RequestParam(name = "id") Long id,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        return ResponseEntity.ok(
                perfilUsuarioService.findByID(
                        id,
                        authorizationHeader
                )
        );
    }

    @PutMapping("/descricao")
    public ResponseEntity<?> editarDescricao(
            @RequestParam(name = "id") Long id,
            @RequestBody PerfilUsuario perfilUsuario,
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        return ResponseEntity.ok(
                perfilUsuarioService.salvar(
                        id,
                        perfilUsuario,
                        authorizationHeader
                )
        );
    }
}