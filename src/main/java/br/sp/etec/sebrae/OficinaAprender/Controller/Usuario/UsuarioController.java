package br.sp.etec.sebrae.OficinaAprender.Controller.Usuario;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.Usuario.UsuarioCriarDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.PerfilUsuario.PerfilUsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Arquivo.ArquivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarios;
    private final PerfilUsuarioService perfis;
    private final ArquivoService arquivos;
    public record ContaEntrada(String email) {}
    @PostMapping(value = "/criar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO criar(@RequestPart("dados") UsuarioCriarDTO dto, @RequestPart("foto") MultipartFile foto) { return usuarios.criar(dto, foto); }
    @GetMapping("/me") public UsuarioResponseDTO me() { return usuarios.buscarSessao(null); }
    @GetMapping("/buscar") public List<Pessoa> buscar(@RequestParam(defaultValue = "") String nome) { return usuarios.buscarProfissionais(nome); }
    @GetMapping("/foto") public ResponseEntity<ByteArrayResource> foto(@RequestParam Long id) { return arquivos.resposta(usuarios.buscarFoto(id), true); }
    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PerfilUsuario foto(@RequestParam Long id, @RequestPart("arquivo") MultipartFile arquivo) { return usuarios.trocarFoto(id, arquivo); }
    @PutMapping("/editar") public UsuarioResponseDTO editar(@RequestParam Long id, @RequestBody ContaEntrada dto) { return usuarios.editar(id, dto.email()); }
    @PutMapping("/senha") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void senha(@RequestBody SenhaEntrada dto) { usuarios.alterarSenha(dto); }
    @DeleteMapping("/deletar") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@RequestParam Long id) { usuarios.deletar(id, null); }
    @GetMapping("/id") public UsuarioResponseDTO id(@RequestParam Long id) { return usuarios.findById(id, null); }
    @GetMapping("/email") public UsuarioResponseDTO email(@RequestParam String email) { return usuarios.findByEmail(email, null); }
    @GetMapping("/descricao") public Object descricao(@RequestParam Long id) { return perfis.findByID(id, null).orElseThrow(); }
    @PutMapping("/descricao") public PerfilUsuario descricao(@RequestParam Long id, @RequestBody PerfilUsuario dto) { return perfis.salvar(id, dto, null); }
}
