package br.sp.etec.sebrae.OficinaAprender.Controller.Login;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Service.Login.AtivacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
public class AtivacaoController {
    private final AtivacaoService service;
    @PostMapping("/paciente/{id}/ativacao") public AtivacaoResposta emitir(@PathVariable Long id) { return service.emitir(id); }
    @PostMapping("/auth/ativar") public LoginResponseDTO ativar(@RequestBody AtivacaoEntrada dto) { return service.ativar(dto); }
}
