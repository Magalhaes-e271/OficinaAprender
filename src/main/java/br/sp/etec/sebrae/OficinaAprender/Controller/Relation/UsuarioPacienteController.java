package br.sp.etec.sebrae.OficinaAprender.Controller.Relation;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.UsuarioPaciente;
import br.sp.etec.sebrae.OficinaAprender.Service.Relation.UsuarioPacienteService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuario-aluno")
public class UsuarioPacienteController {
   public UsuarioPacienteService usuarioPacienteService;
   UsuarioPacienteController(UsuarioPacienteService usuarioPacienteService){
       this.usuarioPacienteService = usuarioPacienteService;
   }
    @PostMapping("/criar")
    public ResponseEntity<?> criar(UsuarioPaciente usuarioPaciente){
    return usuarioPacienteService.criar(usuarioPaciente);
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editar(UsuarioPaciente usuarioPaciente){
       usuarioPacienteService.salvar(usuarioPaciente);
        return ResponseEntity.ok().build();
    }
}
