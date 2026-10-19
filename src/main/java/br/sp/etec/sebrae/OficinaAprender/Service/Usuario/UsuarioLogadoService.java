package br.sp.etec.sebrae.OficinaAprender.Service.Usuario;
import br.sp.etec.sebrae.OficinaAprender.DTO.Usuario.Logado.UsuarioLogadoDTO;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UsuarioLogadoService {
    private final AcessoService acesso;
    public UsuarioLogadoDTO getUsuario() { var u = acesso.atual(); return new UsuarioLogadoDTO(u.getId(), u.getEmail()); }
}
