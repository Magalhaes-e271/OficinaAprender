package br.sp.etec.sebrae.OficinaAprender.Controller.Postagem.Meta;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta.MetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/meta")
@RequiredArgsConstructor
public class MetaController {
    private final MetaService service;
    @GetMapping public List<MetaResposta> listar(@RequestParam Long idPaciente) { return service.listar(idPaciente); }
    @GetMapping("/{id}") public MetaResposta buscar(@PathVariable Long id) { return service.detalhar(id); }
    @PostMapping("/criar") @ResponseStatus(HttpStatus.CREATED)
    public MetaResposta criar(@RequestParam Long idPaciente, @RequestBody MetaEntrada dto) { return service.criar(idPaciente, dto); }
    @PatchMapping("/{id}") public MetaResposta editar(@PathVariable Long id, @RequestBody MetaEntrada dto) { return service.editar(id, dto); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void remover(@PathVariable Long id) { service.remover(id); }
}
