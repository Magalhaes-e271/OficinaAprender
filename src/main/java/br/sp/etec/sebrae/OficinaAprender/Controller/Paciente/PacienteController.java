package br.sp.etec.sebrae.OficinaAprender.Controller.Paciente;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Service.Paciente.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/paciente")
@RequiredArgsConstructor
public class PacienteController {
    private final PacienteService service;
    @GetMapping public List<UsuarioResponseDTO> listar() { return service.listar(); }
    @GetMapping("/{id}") public UsuarioResponseDTO buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping("/criar") @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO criar(@RequestBody PacienteEntrada dto) { return service.criar(dto); }
    @GetMapping("/{id}/equipe") public List<RelacaoResposta> equipe(@PathVariable Long id) { return service.equipe(id); }
    @PostMapping("/{id}/equipe") @ResponseStatus(HttpStatus.CREATED)
    public RelacaoResposta convidar(@PathVariable Long id, @RequestBody ConviteEquipe dto) { return service.convidar(id, dto); }
}
