package br.sp.etec.sebrae.OficinaAprender.Controller;

import br.sp.etec.sebrae.OficinaAprender.Entity.Paciente;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.UsuarioPaciente;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Service.PacienteService;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.DescricaoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Relation.UsuarioPacienteService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;

import br.sp.etec.sebrae.OficinaAprender.res.Role;
import br.sp.etec.sebrae.OficinaAprender.res.TipoAcesso;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteService pacienteService;
    private final UsuarioPacienteService usuarioAlunoService;
    private final UsuarioService usuarioService;
    private final DescricaoService descricaoService;
    public PacienteController(
            PacienteService pacienteService,
            UsuarioService usuarioService,
            UsuarioPacienteService usuarioAlunoService, DescricaoService descricaoService
    ) {
        this.pacienteService = pacienteService;
        this.usuarioService = usuarioService;
        this.usuarioAlunoService = usuarioAlunoService;
        this.descricaoService = descricaoService;
    }

    @PostMapping("/criar")
    public ResponseEntity<?> criar(
            @RequestBody Paciente paciente,
            @RequestParam Long id_usuario
    ) {
        Paciente pacienteNovo = pacienteService.criar(paciente);

        Usuario usuario = usuarioService.findById(id_usuario);

        UsuarioPaciente usuarioPaciente = new UsuarioPaciente();
        usuarioPaciente.setPaciente(pacienteNovo);
        usuarioPaciente.setUsuario(usuario);
        usuarioPaciente.setRole(Role.PROFISSIONAL);
        usuarioPaciente.setTipoAcesso(TipoAcesso.ADMINISTRADOR);

        usuarioAlunoService.salvar(usuarioPaciente);
        descricaoService.criar(paciente.getDescricao());
        return ResponseEntity.ok(pacienteNovo);
    }
}