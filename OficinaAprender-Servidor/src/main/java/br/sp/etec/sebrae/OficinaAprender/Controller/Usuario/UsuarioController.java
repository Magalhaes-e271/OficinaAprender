package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Parente;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Profissional;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Role;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.Tipos.ParenteService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.Tipos.ProfissionalService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ProfissionalService profissionalService;
    private final ParenteService parenteService;

    public UsuarioController(UsuarioService usuarioService, ProfissionalService profissionalService, ParenteService parenteService) {
        this.usuarioService = usuarioService;
        this.profissionalService = profissionalService;
        this.parenteService = parenteService;
    }

    @PostMapping("/criar")
    public ResponseEntity<UsuarioResponseDTO> criarConta(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.salvar(dto);
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setRole(dto.role());
        usuario.setId(response.id());

        if(usuario.getRole() == Role.PROFISSIONAL){
            Profissional profissional = new Profissional();
            profissional.setUsuario(usuario);
            profissional.setNome(dto.nome());
            profissionalService.salvar(profissional);
        }
        if(usuario.getRole() == Role.FAMILIA ){
            Parente parente = new Parente();
            parente.setUsuario(usuario);
            parente.setNome(dto.nome());
            parenteService.salvar(parente);
        }

        return ResponseEntity.ok(response);

    }

}