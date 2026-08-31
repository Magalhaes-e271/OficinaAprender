package br.sp.etec.sebrae.OficinaAprender.Controller.Postagem.anexo;


import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Anexo.AnexoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/anexo")
public class AnexoController {

    private final AnexoService anexoService;

    public AnexoController(AnexoService anexoService) {
        this.anexoService = anexoService;
    }

    @PostMapping("criar")
    public ResponseEntity<Anexo> criar(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam("idPostagem") Long idPostagem
    ) {

        Anexo anexo = anexoService.criar(arquivo, idPostagem);

        return ResponseEntity.ok(anexo);
    }


    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Anexo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(anexoService.findByID(id));
    }

    // BUSCAR ANEXOS DE UMA POSTAGEM
    @GetMapping("/postagem/{idPostagem}")
    public ResponseEntity<List<Anexo>> buscarPorPostagem(
            @PathVariable Long idPostagem
    ) {
        return ResponseEntity.ok(
                anexoService.buscarPorPostagem(idPostagem)
        );
    }

    // EDITAR
    @PutMapping("/{id}")
    public ResponseEntity<Anexo> editar(
            @PathVariable Long id,
            @RequestBody Anexo anexo
    ) {
        return ResponseEntity.ok(
                anexoService.editar(id, anexo)
        );
    }

    // DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        anexoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}