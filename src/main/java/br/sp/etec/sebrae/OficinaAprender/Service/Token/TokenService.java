package br.sp.etec.sebrae.OficinaAprender.Service.Token;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.erro;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class TokenService {
    private static final String ISSUER = "OficinaAprender";
    private final JdbcTemplate jdbc;
    private final String segredoConfigurado;
    private volatile Algorithm algoritmo;
    public TokenService(JdbcTemplate jdbc, @Value("${app.jwt.secret:}") String segredo) {
        this.jdbc = jdbc;
        this.segredoConfigurado = segredo;
        if (!segredo.isBlank() && segredo.getBytes(java.nio.charset.StandardCharsets.UTF_8).length < 32)
            throw new IllegalArgumentException("JWT_SECRET deve conter pelo menos 32 bytes.");
    }
    private Algorithm algoritmo() {
        if (algoritmo == null) synchronized (this) {
            if (algoritmo == null) {
                String segredo = segredoConfigurado;
                if (segredo.isBlank()) {
                    byte[] bytes = new byte[48];
                    new SecureRandom().nextBytes(bytes);
                    String gerado = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
                    // A mesma chave persiste entre reinícios e instâncias; nenhum segredo vai para o Git.
                    jdbc.update("insert into app_segredo(nome, valor) values ('jwt', ?) on conflict (nome) do nothing", gerado);
                    segredo = jdbc.queryForObject("select valor from app_segredo where nome = 'jwt'", String.class);
                }
                algoritmo = Algorithm.HMAC256(segredo);
            }
        }
        return algoritmo;
    }
    public String gerarToken(Usuario usuario) {
        return JWT.create().withIssuer(ISSUER).withSubject(usuario.getEmail())
                .withClaim("id", usuario.getId()).withClaim("versao", usuario.getVersaoToken())
                .withIssuedAt(Instant.now()).withExpiresAt(Instant.now().plus(2, ChronoUnit.HOURS))
                .sign(algoritmo());
    }
    public DecodedJWT verificar(String token) {
        try { return JWT.require(algoritmo()).withIssuer(ISSUER).build().verify(token); }
        catch (JWTVerificationException | IllegalArgumentException e) { return null; }
    }
    public String extrairEmail(String token) {
        var jwt = verificar(token);
        return jwt == null ? null : jwt.getSubject();
    }
    public String validarToken(String token) { return extrairEmail(token); }
    public String validarAutorizacao(String header) {
        if (header == null || !header.regionMatches(true, 0, "Bearer ", 0, 7))
            throw erro(UNAUTHORIZED, "Token não informado.");
        var email = extrairEmail(header.substring(7).trim());
        if (email == null) throw erro(UNAUTHORIZED, "Token inválido ou expirado.");
        return email;
    }
}
