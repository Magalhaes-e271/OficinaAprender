package br.sp.etec.sebrae.OficinaAprender.Controller.Login;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.MfaDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Service.Login.MfaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/mfa")
@RequiredArgsConstructor
public class MfaController {
    private final MfaService service;
    private ResponseEntity<?> semCache(Object resultado) { return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(resultado); }
    @GetMapping public ResponseEntity<?> status() { return semCache(service.status()); }
    @PostMapping("/preparar") public ResponseEntity<?> iniciar(@RequestBody Senha dto) { return semCache(service.iniciar(dto)); }
    @PostMapping("/cancelar") public ResponseEntity<?> cancelar() { service.cancelar(); return ResponseEntity.noContent().cacheControl(CacheControl.noStore()).build(); }
    @PostMapping("/ativar") public ResponseEntity<?> ativar(@RequestBody Codigo dto) { return semCache(service.confirmar(dto)); }
    @PostMapping("/desativar") public ResponseEntity<?> desativar(@RequestBody Prova dto) { return semCache(service.desativar(dto)); }
    @PostMapping("/recuperacao") public ResponseEntity<?> recuperar(@RequestBody Prova dto) { return semCache(service.recuperarCodigos(dto)); }
}
