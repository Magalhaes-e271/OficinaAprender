package br.sp.etec.sebrae.OficinaAprender.Controller;

import br.sp.etec.sebrae.OficinaAprender.Entity.Paciente;
import br.sp.etec.sebrae.OficinaAprender.Service.PacienteService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/aluno")
public class PacienteController {


    @PostMapping(value = "/criar", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> criar
            (@RequestBody Paciente paciente,
             @RequestParam Long id_usuario)
    {
        return PacienteService.criar(paciente);
}
}
