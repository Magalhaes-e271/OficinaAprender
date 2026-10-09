package br.sp.etec.sebrae.OficinaAprender.Service.Login;

import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginRequestDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public LoginService(
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            TokenService tokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponseDTO login(LoginRequestDTO dados) {

        Usuario usuario = usuarioRepository
                .findByEmail(dados.email())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Email ou senha inválidos."
                        )
                );

        if (!passwordEncoder.matches(
                dados.senha(),
                usuario.getSenha()
        )) {
            throw new RuntimeException(
                    "Email ou senha inválidos."
            );
        }

        String token = tokenService.gerarToken(usuario);

        UsuarioResponseDTO usuarioDTO =
                usuarioService.converterParaDTO(usuario);

        return new LoginResponseDTO(
                token,
                usuarioDTO
        );
    }
}