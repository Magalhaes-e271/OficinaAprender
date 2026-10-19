package br.sp.etec.sebrae.OficinaAprender.Controller.Postagem;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.PostagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@RestController
@RequestMapping("/postagem")
@RequiredArgsConstructor
public class PostagemController {
    private final PostagemService service;
    @GetMapping public List<PostagemResposta> listar(@RequestParam Long idMeta) { return service.listar(idMeta); }
    @PostMapping(value = "/publicar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) @ResponseStatus(HttpStatus.CREATED)
    public PostagemResposta publicar(@RequestParam Long idMeta, @RequestPart("dados") PostagemEntrada dto,
                                      @RequestPart(value = "arquivos", required = false) List<MultipartFile> arquivos) { return service.criar(idMeta, dto, arquivos); }
    @PostMapping("/criar") @ResponseStatus(HttpStatus.CREATED)
    public PostagemResposta criar(@RequestParam Long idMeta, @RequestBody PostagemEntrada dto) { return service.criar(idMeta, dto, List.of()); }
    @PatchMapping("/{id}") public PostagemResposta editar(@PathVariable Long id, @RequestBody PostagemEntrada dto) { return service.editar(id, dto); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void remover(@PathVariable Long id) { service.remover(id); }
}
