package br.sp.etec.sebrae.OficinaAprender.Controller;

import br.sp.etec.sebrae.OficinaAprender.Service.FamiliaService;

import br.sp.etec.sebrae.OficinaAprender.entities.PessoaFamilia;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/Familia")
@CrossOrigin(origins = "*")
public class FamiliaController {

    private final FamiliaService familiaService;

    public FamiliaController(FamiliaService familiaService) {
        this.familiaService = familiaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaFamilia> findById(@PathVariable Long id) {
        Optional<PessoaFamilia> familia = familiaService.findById(id);
        return ResponseEntity.ok().body(familia.get());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody PessoaFamilia Familia) {
        try {

            PessoaFamilia novoPessoaFamilia = familiaService.CadastrarFamilia(Familia);
            return ResponseEntity.ok(novoPessoaFamilia);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar: " + e.getMessage());
        }
    }
}
