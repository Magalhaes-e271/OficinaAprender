package br.sp.etec.sebrae.OficinaAprender.Controller.Postagem.anexo;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.AnexoResposta;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Anexo.AnexoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Arquivo.ArquivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController
@RequestMapping("/anexo")
@RequiredArgsConstructor
public class AnexoController {
    private final AnexoService service;
    private final ArquivoService arquivos;
    public record NomeEntrada(String nome) {}
    @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) @ResponseStatus(HttpStatus.CREATED)
    public AnexoResposta criar(@RequestParam Long idPostagem, @RequestPart("arquivo") MultipartFile arquivo) { return service.criar(arquivo, idPostagem); }
    @GetMapping("/{id}") public AnexoResposta buscar(@PathVariable Long id) { return service.findByID(id); }
    @GetMapping("/postagem/{idPostagem}") public List<AnexoResposta> listar(@PathVariable Long idPostagem) { return service.buscarPorPostagem(idPostagem); }
    @GetMapping("/{id}/arquivo") public ResponseEntity<ByteArrayResource> arquivo(@PathVariable Long id) { return arquivos.resposta(service.arquivo(id), false); }
    @PutMapping("/{id}") public AnexoResposta editar(@PathVariable Long id, @RequestBody NomeEntrada dto) { return service.editar(id, dto.nome()); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deletar(@PathVariable Long id) { service.deletar(id); }
}
