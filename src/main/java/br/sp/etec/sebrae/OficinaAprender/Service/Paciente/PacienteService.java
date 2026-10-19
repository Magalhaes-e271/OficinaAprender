package br.sp.etec.sebrae.OficinaAprender.Service.Paciente;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.*;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.PerfilUsuario.PerfilUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Relacao.RelacaoUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Relacao.RelacaoUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class PacienteService {
    private final UsuarioRepository usuarios;
    private final PerfilUsuarioRepository perfis;
    private final RelacaoUsuarioRepository relacoes;
    private final RelacaoUsuarioService vinculos;
    private final UsuarioService pessoas;
    private final PasswordEncoder encoder;
    private final AcessoService acesso;
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        var u = acesso.atual();
        if (u.getTipoUsuario() == TipoUsuario.PACIENTE) return List.of(pessoas.converterParaDTO(u));
        return relacoes.participantes(u.getId()).stream()
                .filter(r -> r.getTipoRelacao() == TipoRelacao.PROFISSIONAL_PACIENTE && r.getSolicitacao() == Solicitacao.ACEITA)
                .map(r -> r.getUsuarioSolicitante().getId().equals(u.getId()) ? r.getUsuarioDestinatario() : r.getUsuarioSolicitante())
                .filter(p -> p.isAtivo() && p.getTipoUsuario() == TipoUsuario.PACIENTE)
                .collect(java.util.stream.Collectors.toMap(Usuario::getId, p -> p, (a,b) -> a, LinkedHashMap::new))
                .values().stream().map(pessoas::converterParaDTO).toList();
    }
    public UsuarioResponseDTO buscar(Long id) { return pessoas.converterParaDTO(acesso.paciente(id)); }
    @Transactional
    public UsuarioResponseDTO criar(PacienteEntrada dto) {
        var autor = acesso.profissional();
        if (dto == null) throw erro(BAD_REQUEST, "Informe os dados do paciente.");
        String email = email(dto.email()); perfil(dto.perfilUsuario(), false);
        List<Long> equipe = dto.profissionais() == null ? List.of() : dto.profissionais().stream().distinct().toList();
        if (equipe.size() > 30 || equipe.contains(autor.getId())) throw erro(BAD_REQUEST, "Equipe inválida. Você já será vinculado ao paciente.");
        List<Usuario> profissionais = equipe.stream().map(acesso::usuario).toList();
        if (profissionais.stream().anyMatch(u -> u.getTipoUsuario() != TipoUsuario.PROFISSIONAL)) throw erro(BAD_REQUEST, "Selecione somente profissionais.");
        // Serializa cadastros do mesmo profissional e permite repetir após uma perda de resposta.
        java.util.stream.Stream.concat(java.util.stream.Stream.of(autor.getId()), equipe.stream()).distinct().sorted().forEach(id -> usuarios.bloquear(id).orElseThrow());
        var existente = usuarios.findByEmail(email).orElse(null);
        if (existente != null) {
            var p = perfis.findById(existente.getId()).orElse(null);
            if (Objects.equals(existente.getCriadoPorId(), autor.getId()) && existente.getTipoUsuario() == TipoUsuario.PACIENTE && existente.isAtivo()
                    && p != null && Objects.equals(p.getNome(), dto.perfilUsuario().getNome())
                    && Objects.equals(p.getDataNascimento(), dto.perfilUsuario().getDataNascimento())
                    && Objects.equals(p.getCondicao(), dto.perfilUsuario().getCondicao())
                    && Objects.equals(p.getConteudoHtml(), dto.perfilUsuario().getConteudoHtml())) return pessoas.converterParaDTO(existente);
            throw erro(CONFLICT, "Já existe um usuário com esse e-mail. Não foi criado outro paciente.");
        }
        var paciente = new Usuario(); paciente.setEmail(email); paciente.setTipoUsuario(TipoUsuario.PACIENTE);
        paciente.setCriadoPorId(autor.getId()); paciente.setAcessoHabilitado(false);
        paciente.setSenha(encoder.encode(UUID.randomUUID().toString() + UUID.randomUUID()));
        paciente = usuarios.saveAndFlush(paciente);
        var p = new PerfilUsuario(); UsuarioService.copiar(dto.perfilUsuario(), p); p.setUsuario(paciente);
        perfis.saveAndFlush(p);
        vinculos.vincular(autor, paciente, TipoRelacao.PROFISSIONAL_PACIENTE, Solicitacao.ACEITA, autor.getId());
        for (var profissional : profissionais) vinculos.vincular(paciente, profissional, TipoRelacao.PROFISSIONAL_PACIENTE, Solicitacao.PRENDENTE, autor.getId());
        return pessoas.converterParaDTO(paciente);
    }
    @Transactional(readOnly = true)
    public List<RelacaoResposta> equipe(Long id) {
        acesso.paciente(id);
        return relacoes.participantes(id).stream().filter(r -> r.getTipoRelacao() == TipoRelacao.PROFISSIONAL_PACIENTE).map(vinculos::resposta).toList();
    }
    @Transactional
    public RelacaoResposta convidar(Long id, ConviteEquipe dto) {
        var paciente = acesso.editarPaciente(id); var autor = acesso.profissional();
        var destino = acesso.usuario(dto.idProfissional());
        if (destino.getTipoUsuario() != TipoUsuario.PROFISSIONAL) throw erro(BAD_REQUEST, "Selecione um profissional.");
        return vinculos.resposta(vinculos.vincular(paciente, destino, TipoRelacao.PROFISSIONAL_PACIENTE, Solicitacao.PRENDENTE, autor.getId()));
    }
}
