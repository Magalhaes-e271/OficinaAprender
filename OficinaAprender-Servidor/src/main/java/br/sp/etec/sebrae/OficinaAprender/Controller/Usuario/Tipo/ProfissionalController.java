package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario.Tipo;


import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.Tipos.ProfissionalService;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Profissional;

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
    @RequestMapping("/criar")
    public Profissional CriarConta(@RequestBody Profissional profissional){
        return profissionalService.salvar(profissional);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profissional> findById(@PathVariable Long id) {
    Optional<Profissional> profissional = profissionalService.findById(id);
    return ResponseEntity.ok().body(profissional.orElse(null));
    }

}

