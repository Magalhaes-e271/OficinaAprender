package br.sp.etec.sebrae.OficinaAprender.Controller;

import br.sp.etec.sebrae.OficinaAprender.Service.ParenteService;

import br.sp.etec.sebrae.OficinaAprender.entities.Parente;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/Familia")
@CrossOrigin(origins = "*")
public class ParenteController {

    private final ParenteService parenteService;

    public ParenteController(ParenteService parenteService) {
        this.parenteService = parenteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Parente> findById(@PathVariable Long id) {
        Optional<Parente> familia = parenteService.findById(id);
        return ResponseEntity.ok().body(familia.get());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Parente Familia) {
        try {

            Parente novoParente = parenteService.CadastrarFamilia(Familia);
            return ResponseEntity.ok(novoParente);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar: " + e.getMessage());
        }
    }
}
