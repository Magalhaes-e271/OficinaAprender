package br.sp.etec.sebrae.OficinaAprender.Controller.Agenda;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Service.Agenda.AgendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {
    private final AgendaService service;
    @GetMapping public List<AgendaResposta> listar() { return service.listar(); }
    @PostMapping("/criar") @ResponseStatus(HttpStatus.CREATED) public AgendaResposta criar(@RequestBody AgendaEntrada dto) { return service.criar(dto); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void remover(@PathVariable Long id) { service.remover(id); }
}
