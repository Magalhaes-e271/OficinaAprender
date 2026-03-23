package br.sp.etec.sebrae.OficinaAprender.Controller;


import br.sp.etec.sebrae.OficinaAprender.Service.ProfissionalService;
import br.sp.etec.sebrae.OficinaAprender.entities.Profissional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/Profissional")
@CrossOrigin(origins = "*")
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profissional> findById(@PathVariable Long id) {
    Optional<Profissional> profissional = profissionalService.findById(id);
    return ResponseEntity.ok().body(profissional.get());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Profissional Pro) {
       try {

           Profissional NovoProfissional = profissionalService.cadastroProfissional(Pro);
           return ResponseEntity.ok(NovoProfissional);
       }catch (Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar: " + e.getMessage());
         }
     }
}

