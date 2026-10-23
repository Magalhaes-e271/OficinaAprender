package br.sp.etec.sebrae.OficinaAprender.Controller.Login;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Service.Login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final LoginService service;
    /** Adaptador para clientes antigos que recebem token e usuário no nível principal. */
    @PostMapping("/auth/login") public UsuarioResponseDTO login(@RequestBody LoginRequestDTO dto) {
        var resposta = service.login(dto); var u = resposta.usuario();
        return new UsuarioResponseDTO(u.id(), u.email(), u.tipoUsuario(), u.perfil(), resposta.token());
    }
}
