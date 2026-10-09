package br.sp.etec.sebrae.OficinaAprender.Controller.Login;

import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Service.Login.LoginService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "*")
public class    LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public LoginResponseDTO login(
            @RequestBody LoginRequestDTO dados
    ) {
        System.out.println("### ENTROU NO LOGIN CONTROLLER ###");
        LoginResponseDTO resposta = loginService.login(dados);

        return resposta;
    }
}