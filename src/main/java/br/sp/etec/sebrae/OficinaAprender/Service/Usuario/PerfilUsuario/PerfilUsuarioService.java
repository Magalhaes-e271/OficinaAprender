package br.sp.etec.sebrae.OficinaAprender.Service.Usuario.PerfilUsuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.PerfilUsuario.PerfilUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class PerfilUsuarioService {
    private final PerfilUsuarioRepository perfis;
    private final AcessoService acesso;
    public Optional<PerfilUsuario> findByID(Long id, String ignorado) { acesso.lerPerfil(id); return perfis.findById(id); }
    @Transactional
    public PerfilUsuario salvar(Long id, PerfilUsuario dto, String ignorado) {
        acesso.editarPerfil(id);
        var p = perfis.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Perfil não encontrado."));
        if (dto.getNome() != null) p.setNome(dto.getNome());
        if (dto.getTelefone() != null) p.setTelefone(dto.getTelefone());
        if (dto.getCpf() != null) p.setCpf(dto.getCpf());
        if (dto.getDataNascimento() != null) p.setDataNascimento(dto.getDataNascimento());
        if (dto.getOcupacao() != null) p.setOcupacao(dto.getOcupacao());
        if (dto.getInstituicao() != null) p.setInstituicao(dto.getInstituicao());
        if (dto.getConteudoHtml() != null) p.setConteudoHtml(dto.getConteudoHtml());
        if (dto.getHorarioDeEntrada() != null) p.setHorarioDeEntrada(dto.getHorarioDeEntrada());
        if (dto.getHorarioDeSaida() != null) p.setHorarioDeSaida(dto.getHorarioDeSaida());
        if (dto.getCondicao() != null) p.setCondicao(dto.getCondicao());
        perfil(p, acesso.usuario(id).getTipoUsuario() == TipoUsuario.PROFISSIONAL);
        return perfis.save(p);
    }
    @Transactional
    public ResponseEntity<?> criar(PerfilUsuario dto, String ignorado) {
        Long id = acesso.atual().getId();
        if (perfis.existsById(id)) throw erro(CONFLICT, "Este usuário já tem um perfil.");
        perfil(dto, acesso.atual().getTipoUsuario() == TipoUsuario.PROFISSIONAL);
        var p = new PerfilUsuario(); UsuarioService.copiar(dto, p); p.setUsuario(acesso.usuario(id));
        return ResponseEntity.status(CREATED).body(perfis.save(p));
    }
}
