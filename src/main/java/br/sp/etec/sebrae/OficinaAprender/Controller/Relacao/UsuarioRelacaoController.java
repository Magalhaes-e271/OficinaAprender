package br.sp.etec.sebrae.OficinaAprender.Controller.Relacao;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoRelacao;
import br.sp.etec.sebrae.OficinaAprender.Service.Relacao.RelacaoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/usuario-aluno")
@RequiredArgsConstructor
public class UsuarioRelacaoController {
    private final RelacaoUsuarioService service;
    public record AceiteLegado(Long id) {}
    @GetMapping public List<RelacaoResposta> listar(@RequestParam(required = false) TipoRelacao tipoRelacao) { return service.listar(tipoRelacao); }
    @PostMapping("/criar") @ResponseStatus(HttpStatus.CREATED)
    public RelacaoResposta criar(@RequestBody RelacaoEntrada dto) { return service.criar(dto); }
    @PutMapping("/{id}/aceitar") public RelacaoResposta aceitar(@PathVariable Long id) { return service.aceitar(id); }
    @PutMapping("/editar") public RelacaoResposta editar(@RequestBody AceiteLegado dto) { return service.aceitar(dto.id()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) { service.remover(id); }
}
