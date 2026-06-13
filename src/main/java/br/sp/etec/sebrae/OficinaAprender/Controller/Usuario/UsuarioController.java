package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import org.apache.naming.HandlerRef;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuarios")
public class UsuarioController {
    /*
    X Cadastrar usuário
    X Buscar usuário(email/Id)
    X Atualizar usuário
    X Excluir usuário
    Buscar descrição do usuário
    Atualizar descrição do usuário
    * Buscar usuario pelo nome (return list de usuarios, caracter por caracter)
*/

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

//    CRUD
    @PostMapping("/criar")
    public ResponseEntity<?> criarConta(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.Criar(dto);
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setId(response.id());

        return ResponseEntity.ok(response);

    }
    @PutMapping("/{id}/editar")
    public ResponseEntity<?> editarConta( @PathVariable Long id, @RequestBody Usuario usuario){
    return ResponseEntity.ok(usuarioService.editar(id ,usuario));
    }
    @DeleteMapping("/{id}/deletar")
    public ResponseEntity<?> deletarConta(@PathVariable Long id){
        usuarioService.deletar(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email){
        if(usuarioService.findByEmail(email).getEmail() == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Não foi possivel encontra um usuario com esse email");
        }
        return
                ResponseEntity.ok(usuarioService.findByEmail(email));
    }
    @GetMapping("{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){
        if(usuarioService.findById(id).getId() == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Não foi possivel encontra um usuario com esse id");
        }
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    //buscar descrição do usuario
    @GetMapping("/{id}/descricao")
    public ResponseEntity<?> buscarDescricao(@PathVariable Long id){
        return ResponseEntity.ok(usuarioService.findById(id).getDescricao());
    }
}