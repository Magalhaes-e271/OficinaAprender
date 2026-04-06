package br.sp.etec.sebrae.OficinaAprender.Controller;

import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/criar-conta")
    public ResponseEntity<UsuarioResponseDTO> criarConta(@RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.criarConta(dto);
        return ResponseEntity.ok(response);
    }
}