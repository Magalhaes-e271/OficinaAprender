package br.sp.etec.sebrae.OficinaAprender.Controller.Login;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.*;
import br.sp.etec.sebrae.OficinaAprender.Service.Login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService service;
    @PostMapping("/login") public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) { return service.login(dto); }
}
