package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario.PerfilUsuario;


import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.PerfilUsuario.PerfilUsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/perfil")
public class PerfilUsuarioController {
    private final PerfilUsuarioService perfilUsuarioService;

    public PerfilUsuarioController(PerfilUsuarioService perfilUsuarioService) {
        this.perfilUsuarioService = perfilUsuarioService;
    }


    @PostMapping
    public ResponseEntity<?> buscarPorId(@RequestParam Long id){
        return ResponseEntity.ok(perfilUsuarioService.findByID(id));
    }

    @PutMapping("/editar")
    public ResponseEntity<?> editar(@RequestParam Long id, PerfilUsuario perfilEditado){

        if(perfilUsuarioService.findByID(id).isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Não foi possivel achar essa descricao");
        }else {

        return ResponseEntity.ok(perfilUsuarioService.salvar(id, perfilEditado));

        }
    }
}
