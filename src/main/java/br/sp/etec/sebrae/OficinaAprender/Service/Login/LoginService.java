package br.sp.etec.sebrae.OficinaAprender.Service.Login;

import br.sp.etec.sebrae.OficinaAprender.DTO.Login.*;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import br.sp.etec.sebrae.OficinaAprender.Security.ControleTentativas;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.erro;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UsuarioRepository usuarios;
    private final UsuarioService service;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final ControleTentativas limites;
    private final MfaService mfa;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;
    // Executa BCrypt também para conta inexistente, evitando diferenças triviais de tempo.
    private static final String HASH_INEXISTENTE = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
    public LoginResponseDTO login(LoginRequestDTO dados) {
        if (dados == null || dados.email() == null || dados.senha() == null) throw erro(UNAUTHORIZED, "E-mail ou senha inválidos.");
        String email = dados.email().trim().toLowerCase(Locale.ROOT);
        limites.conta(email);
        if (email.length() > 254 || dados.senha().getBytes(StandardCharsets.UTF_8).length > 72) throw erro(UNAUTHORIZED, "E-mail ou senha inválidos.");
        var resposta = new TransactionTemplate(transactionManager).execute(status -> {
            var u = usuarios.findByEmail(email).orElse(null);
            if (u != null) entityManager.refresh(u, LockModeType.PESSIMISTIC_WRITE);
            boolean confere = encoder.matches(dados.senha(), u == null ? HASH_INEXISTENTE : u.getSenha());
            if (!confere || u == null || !u.isAtivo() || !u.isAcessoHabilitado()) throw erro(UNAUTHORIZED, "E-mail ou senha inválidos.");
            mfa.exigir(u, dados.codigo());
            return new LoginResponseDTO(tokens.gerarToken(u), service.converterParaDTO(u));
        });
        limites.sucesso(email);
        return resposta;
    }
}
