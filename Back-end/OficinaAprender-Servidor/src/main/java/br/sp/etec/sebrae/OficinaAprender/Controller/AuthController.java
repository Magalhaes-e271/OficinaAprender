package br.sp.etec.sebrae.OficinaAprender.Controller;


import br.sp.etec.sebrae.OficinaAprender.DTO.LoginRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}