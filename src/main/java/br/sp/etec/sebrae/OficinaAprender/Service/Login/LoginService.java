package br.sp.etec.sebrae.OficinaAprender.Service.Login;

import br.sp.etec.sebrae.OficinaAprender.DTO.Login.*;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.erro;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final UsuarioRepository usuarios;
    private final UsuarioService service;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    // Executa BCrypt também para conta inexistente, evitando diferenças triviais de tempo.
    private static final String HASH_INEXISTENTE = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
    public LoginResponseDTO login(LoginRequestDTO dados) {
        if (dados == null || dados.email() == null || dados.senha() == null) throw erro(UNAUTHORIZED, "E-mail ou senha inválidos.");
        var u = usuarios.findByEmail(dados.email().trim().toLowerCase(Locale.ROOT)).orElse(null);
        boolean confere = encoder.matches(dados.senha(), u == null ? HASH_INEXISTENTE : u.getSenha());
        if (!confere || u == null || !u.isAtivo() || !u.isAcessoHabilitado()) throw erro(UNAUTHORIZED, "E-mail ou senha inválidos.");
        return new LoginResponseDTO(tokens.gerarToken(u), service.converterParaDTO(u));
    }
}
