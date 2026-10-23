package br.sp.etec.sebrae.OficinaAprender.Service.Login;

import br.sp.etec.sebrae.OficinaAprender.DTO.Login.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.SenhaEntrada;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import br.sp.etec.sebrae.OficinaAprender.Security.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.sql.Timestamp;
import java.security.SecureRandom;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.erro;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class MfaService {
    private final JdbcTemplate jdbc;
    private final UsuarioRepository usuarios;
    private final UsuarioService pessoas;
    private final TokenService tokens;
    private final AcessoService acesso;
    private final PasswordEncoder encoder;
    private final MfaCifra cifra;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public MfaDtos.Status status() {
        long id = acesso.profissional().getId();
        boolean ativo = Boolean.TRUE.equals(jdbc.query("SELECT ativo FROM auth_mfa WHERE usuario_id = ?", rs -> rs.next() && rs.getBoolean(1), id));
        return new MfaDtos.Status(ativo, cifra.disponivel(), ativo ? jdbc.queryForObject("SELECT count(*) FROM auth_mfa_recuperacao WHERE usuario_id = ?", Integer.class, id) : 0);
    }
    private Usuario bloquearAtual(boolean profissional) {
        long id = (profissional ? acesso.profissional() : acesso.atual()).getId();
        var u = usuarios.bloquear(id).orElseThrow(); entityManager.refresh(u);
        if (!u.isAtivo() || !u.isAcessoHabilitado()) throw erro(UNAUTHORIZED, "Sessão inválida.");
        return u;
    }
    private Map<String,Object> registro(long id) {
        var rows = jdbc.queryForList("SELECT * FROM auth_mfa WHERE usuario_id = ? FOR UPDATE", id);
        return rows.isEmpty() ? null : rows.getFirst();
    }
    private void provarSenha(Usuario u, String senha) {
        if (senha == null || senha.getBytes(StandardCharsets.UTF_8).length > 72 || !encoder.matches(senha, u.getSenha()))
            throw erro(BAD_REQUEST, "Senha atual incorreta.");
    }
    @Transactional
    public MfaDtos.Preparacao iniciar(MfaDtos.Senha dto) {
        var u = bloquearAtual(true); provarSenha(u, dto.senhaAtual());
        var r = registro(u.getId());
        if (r != null && Boolean.TRUE.equals(r.get("ativo"))) throw erro(CONFLICT, "A verificação em duas etapas já está ativa.");
        String segredo = Totp.novoSegredo(); Instant expira = Instant.now().plusSeconds(600);
        jdbc.update("""
            INSERT INTO auth_mfa(usuario_id, ativo, segredo, expira_em, ultimo_passo) VALUES (?, FALSE, ?, ?, -1)
            ON CONFLICT (usuario_id) DO UPDATE SET segredo = excluded.segredo, expira_em = excluded.expira_em, ultimo_passo = -1
            """, u.getId(), cifra.cifrar(segredo, u.getId()), Timestamp.from(expira));
        String label = URLEncoder.encode("OficinaAprender:" + u.getEmail(), StandardCharsets.UTF_8).replace("+", "%20");
        return new MfaDtos.Preparacao(segredo, "otpauth://totp/" + label + "?secret=" + segredo + "&issuer=OficinaAprender&algorithm=SHA1&digits=6&period=30", expira);
    }
    @Transactional
    public void cancelar() {
        var u = bloquearAtual(true);
        jdbc.update("DELETE FROM auth_mfa WHERE usuario_id = ? AND ativo = FALSE", u.getId());
    }
    @Transactional
    public MfaDtos.Resultado confirmar(MfaDtos.Codigo dto) {
        var u = bloquearAtual(true);
        var r = registro(u.getId());
        if (r == null || Boolean.TRUE.equals(r.get("ativo")) || r.get("expira_em") == null || !((Timestamp)r.get("expira_em")).toInstant().isAfter(Instant.now()))
            throw erro(BAD_REQUEST, "Preparação expirada. Inicie a configuração novamente.");
        long passo = Totp.verificar(cifra.abrir((String)r.get("segredo"), u.getId()), normalizar(dto.codigo()), Instant.now(), -1);
        if (passo < 0) throw erro(BAD_REQUEST, "Código inválido. Confira o horário do celular e tente o próximo código.");
        jdbc.update("UPDATE auth_mfa SET ativo = TRUE, expira_em = NULL, ultimo_passo = ? WHERE usuario_id = ?", passo, u.getId());
        var codigos = novosCodigos(u.getId());
        return new MfaDtos.Resultado(renovar(u), codigos);
    }
    /** Chamado com o usuário bloqueado na transação de login ou da operação sensível. */
    @Transactional
    public void exigir(Usuario u, String codigo) {
        var r = registro(u.getId());
        if (r == null || !Boolean.TRUE.equals(r.get("ativo"))) return;
        if (codigo == null || codigo.isBlank()) throw new MfaDtos.Exigido();
        String normalizado = normalizar(codigo);
        if (normalizado.matches("[0-9]{6}")) {
            long passo = Totp.verificar(cifra.abrir((String)r.get("segredo"), u.getId()), normalizado, Instant.now(), ((Number)r.get("ultimo_passo")).longValue());
            if (passo >= 0) {
                jdbc.update("UPDATE auth_mfa SET ultimo_passo = ? WHERE usuario_id = ?", passo, u.getId()); return;
            }
        } else if (normalizado.matches("[A-F0-9]{20}") && jdbc.update("DELETE FROM auth_mfa_recuperacao WHERE usuario_id = ? AND codigo_hash = ?", u.getId(), ControleTentativas.hash(u.getId() + ":" + normalizado)) == 1) return;
        throw erro(BAD_REQUEST, "Código inválido, expirado ou já utilizado. Use o próximo código ou um código de recuperação disponível.");
    }
    @Transactional
    public LoginResponseDTO desativar(MfaDtos.Prova dto) {
        var u = bloquearAtual(true); provarSenha(u, dto.senhaAtual()); exigirAtivo(u); exigir(u, dto.codigo());
        jdbc.update("DELETE FROM auth_mfa WHERE usuario_id = ?", u.getId()); return renovar(u);
    }
    @Transactional
    public MfaDtos.Resultado recuperarCodigos(MfaDtos.Prova dto) {
        var u = bloquearAtual(true); provarSenha(u, dto.senhaAtual()); exigirAtivo(u); exigir(u, dto.codigo());
        var codigos = novosCodigos(u.getId()); return new MfaDtos.Resultado(renovar(u), codigos);
    }
    @Transactional
    public void alterarSenha(SenhaEntrada dto) {
        var u = bloquearAtual(false); provarSenha(u, dto.senhaAtual()); exigir(u, dto.codigo());
        pessoas.alterarSenha(dto);
    }
    private void exigirAtivo(Usuario u) {
        var r = registro(u.getId()); if (r == null || !Boolean.TRUE.equals(r.get("ativo"))) throw erro(CONFLICT, "A verificação em duas etapas não está ativa.");
    }
    private LoginResponseDTO renovar(Usuario u) {
        u.setVersaoToken(u.getVersaoToken() + 1); usuarios.saveAndFlush(u);
        return new LoginResponseDTO(tokens.gerarToken(u), pessoas.converterParaDTO(u));
    }
    private List<String> novosCodigos(long id) {
        jdbc.update("DELETE FROM auth_mfa_recuperacao WHERE usuario_id = ?", id);
        var lista = new ArrayList<String>(); var random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            byte[] bytes = new byte[10]; random.nextBytes(bytes); String codigo = HexFormat.of().withUpperCase().formatHex(bytes);
            jdbc.update("INSERT INTO auth_mfa_recuperacao(usuario_id,codigo_hash) VALUES (?,?)", id, ControleTentativas.hash(id + ":" + codigo));
            lista.add(String.join("-", codigo.split("(?<=\\G.{4})")));
        }
        return lista;
    }
    private String normalizar(String codigo) { return codigo == null || codigo.length() > 80 ? "" : codigo.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT); }
}
