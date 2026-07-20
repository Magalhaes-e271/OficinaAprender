package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.DescricaoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import ch.qos.logback.core.spi.ErrorCodes;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import org.apache.naming.HandlerRef;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuarios")
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
    private final DescricaoService descricaoService;

    public UsuarioController(UsuarioService usuarioService, DescricaoService descricaoService) {
        this.usuarioService = usuarioService;
        this.descricaoService = descricaoService;
    }

/**    <h1>CRUD</h1> */
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
    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email){
        if(usuarioService.findByEmail(email).getBody() == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Não foi possivel encontra um usuario com esse email");
        }
        return ResponseEntity.ok(
                usuarioService.findByEmail(email));
    }
    @GetMapping("/id/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){

        return ResponseEntity.ok(usuarioService.findById(id));
    }
    /** <p style="font-weight: bold;">Buscar descrição do usuario</p> */
    @GetMapping("/{id}/descricao")
    public ResponseEntity<?> buscarDescricao(@PathVariable Long id){
        Usuario usr = (Usuario) buscarPorId(id).getBody();
        assert usr != null;
        return DescricaoService.findByEmail(usr.getEmail());
    }
    /** <p style="font-weight: bold;"> Atulizar descrição do usuario</p> */
    @PutMapping("/{id}/descricao")
    public ResponseEntity<?> editarDescricao(@PathVariable Long id, @RequestBody Descricao descricao) {
        return DescricaoService.salvar(descricao);
    }
}