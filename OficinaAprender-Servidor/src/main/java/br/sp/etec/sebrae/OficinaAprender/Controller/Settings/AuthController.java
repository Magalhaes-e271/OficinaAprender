package br.sp.etec.sebrae.OficinaAprender.Controller.Settings;

import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.DTO.LoginRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Role;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Settings.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public AuthController(UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dados) {

        Usuario usuario = usuarioRepository.findByEmail(dados.email())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou senha inválidos.");
        }

        if (!usuario.getSenha().equals(dados.senha())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email ou senha inválidos.");
        }

        String token = tokenService.gerarToken(usuario);

        LoginResponseDTO resposta = new LoginResponseDTO(
                token,
                usuario.getEmail()
                );

        return ResponseEntity.ok(resposta);
    }
}