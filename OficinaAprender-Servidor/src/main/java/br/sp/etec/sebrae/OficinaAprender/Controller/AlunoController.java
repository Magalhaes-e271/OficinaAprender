package br.sp.etec.sebrae.OficinaAprender.Controller;




import br.sp.etec.sebrae.OficinaAprender.Entity.Aluno;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Role;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/aluno")
public class AlunoController {

    private final UsuarioRepository usuarioRepository;


    public AlunoController(UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @RequestMapping("/criar")
    public ResponseEntity<?> criar
            (@RequestBody Aluno aluno,
             @RequestParam Long id_usuario)
    {
        Usuario usuario = usuarioRepository.findById(id_usuario).orElse(null);
       if (usuario == null){
           return ResponseEntity
                   .status(HttpStatus.NOT_FOUND)
                   .body("Usuário não encontrado.");
       }

        if(usuario.getRole() != Role.PROFISSIONAL) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Apenas profissionais podem cadastrar alunos.");
        }
        Aluno alunoSalvo = AlunoService.salvar(aluno);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(alunoSalvo);
    }
}
