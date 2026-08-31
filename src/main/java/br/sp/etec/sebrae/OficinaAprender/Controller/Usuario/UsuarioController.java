package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.PerfilUsuario.PerfilUsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuario")
public class UsuarioController {
    /**

     * <p>
     *    <b style = "font-size: 20px">Fucionalidades</b><br>
     *    <b> X Cadastrar usuário</b><br>
     *    <b> X Buscar usuário(email/Id)</b><br>
     *    <b> X Atualizar usuário</b><br>
     *    <b> X Excluir usuário</b><br>
     *    <b>   Atualizar descrição do usuário</b><br>
     *    <b> * Buscar usuario pelo nome (return list de usuarios, caracter por caracter)</b><br>
     * </p>
*/

    private final UsuarioService usuarioService;
    private final PerfilUsuarioService perfilUsuarioService;

    public UsuarioController(UsuarioService usuarioService, PerfilUsuarioService perfilUsuarioService) {
        this.usuarioService = usuarioService;
        this.perfilUsuarioService = perfilUsuarioService;
    }

/**    <h1>CRUD</h1> */
    @PostMapping("/criar")
    public ResponseEntity<?> criarConta(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.criar(dto);
        return ResponseEntity.ok(response);

    }
    @PutMapping("/editar")
    public ResponseEntity<?> editarConta( @RequestParam Long id, @RequestBody Usuario usuario){
    return ResponseEntity.ok(usuarioService.editar(id ,usuario));
    }
    @DeleteMapping("/deletar")
    public ResponseEntity<?> deletarConta(@PathVariable Long id){
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/email")
    public ResponseEntity<?> buscarPorEmail(@RequestParam String email){
        UsuarioResponseDTO dto = usuarioService.findByEmail(email);
       if(dto == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Não foi possivel encontra um usuario com esse email");
        }
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/id")
    public ResponseEntity<?> buscarPorId(@RequestParam Long id){

        return ResponseEntity.ok(usuarioService.findById(id));
    }
    /** <p style="font-weight: bold;">Buscar descrição do usuario</p> */
    @GetMapping("/descricao")
    public ResponseEntity<?> buscarDescricao(@RequestParam Long id){
        Usuario usr = usuarioService.findById(id);
        if(usr == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(perfilUsuarioService.findByID(id));
    }
    /** <p style="font-weight: bold;"> Atulizar descrição do usuario</p> */
    @PutMapping("/descricao")
    public ResponseEntity<?> editarDescricao(@RequestParam Long id, @RequestBody PerfilUsuario perfilUsuario) {
        return ResponseEntity.ok(perfilUsuarioService.salvar(id,perfilUsuario));
    }
}