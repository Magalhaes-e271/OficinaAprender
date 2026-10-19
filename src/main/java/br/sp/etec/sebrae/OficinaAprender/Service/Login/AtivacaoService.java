package br.sp.etec.sebrae.OficinaAprender.Service.Login;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.security.*;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;
@Service
@RequiredArgsConstructor
public class AtivacaoService {
    private final AcessoService acesso;
    private final UsuarioRepository usuarios;
    private final UsuarioService pessoas;
    private final TokenService tokens;
    private final PasswordEncoder encoder;
    private final JdbcTemplate jdbc;
    @Transactional
    public AtivacaoResposta emitir(Long id) {
        var paciente = acesso.editarPaciente(id);
        if (!Objects.equals(paciente.getCriadoPorId(), acesso.atual().getId())) throw erro(FORBIDDEN, "Somente o profissional que cadastrou o paciente pode emitir sua ativação.");
        usuarios.bloquear(id).orElseThrow();
        if (paciente.isAcessoHabilitado()) throw erro(CONFLICT, "O paciente já ativou o acesso.");
        byte[] aleatorio = new byte[32]; new SecureRandom().nextBytes(aleatorio);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(aleatorio);
        Instant expira = Instant.now().plus(Duration.ofHours(24));
        jdbc.update("delete from ativacao_conta where usuario_id = ?", id);
        jdbc.update("insert into ativacao_conta(token_hash, usuario_id, expira_em) values (?, ?, ?)", hash(token), id, java.sql.Timestamp.from(expira));
        return new AtivacaoResposta(token, expira, paciente.getEmail());
    }
    @Transactional
    public LoginResponseDTO ativar(AtivacaoEntrada dto) {
        if (dto.token() == null || dto.token().length() > 100) throw erro(BAD_REQUEST, "Link inválido ou expirado.");
        senha(dto.senha());
        var registros = jdbc.queryForList("select usuario_id, expira_em from ativacao_conta where token_hash = ? for update", hash(dto.token()));
        if (registros.isEmpty()) throw erro(BAD_REQUEST, "Link inválido ou expirado.");
        var registro = registros.getFirst();
        Instant expira = ((java.sql.Timestamp) registro.get("expira_em")).toInstant();
        if (!expira.isAfter(Instant.now())) throw erro(BAD_REQUEST, "Link inválido ou expirado.");
        var u = usuarios.bloquear(((Number) registro.get("usuario_id")).longValue()).orElseThrow();
        if (!u.isAtivo() || u.isAcessoHabilitado()) throw erro(BAD_REQUEST, "Link inválido ou já utilizado.");
        u.setSenha(encoder.encode(dto.senha())); u.setAcessoHabilitado(true); u.setVersaoToken(u.getVersaoToken() + 1);
        usuarios.saveAndFlush(u); jdbc.update("delete from ativacao_conta where usuario_id = ?", u.getId());
        return new LoginResponseDTO(tokens.gerarToken(u), pessoas.converterParaDTO(u));
    }
    @Transactional(readOnly = true)
    public AtivacaoResumo consultar(AtivacaoConsulta dto) {
        if (dto.token() == null || dto.token().length() > 100) throw erro(BAD_REQUEST, "Link inválido ou expirado.");
        var ids = jdbc.queryForList("select usuario_id from ativacao_conta where token_hash = ? and expira_em > CURRENT_TIMESTAMP", Long.class, hash(dto.token()));
        if (ids.isEmpty()) throw erro(BAD_REQUEST, "Link inválido ou expirado.");
        var u = usuarios.findById(ids.getFirst()).filter(p -> p.isAtivo() && !p.isAcessoHabilitado())
                .orElseThrow(() -> erro(BAD_REQUEST, "Link inválido ou já utilizado."));
        var perfil = pessoas.converterParaDTO(u).perfil();
        return new AtivacaoResumo(perfil == null ? "Paciente" : perfil.getNome(), u.getEmail());
    }
    private String hash(String token) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
}
