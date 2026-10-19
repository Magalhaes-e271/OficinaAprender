package br.sp.etec.sebrae.OficinaAprender.Security;

import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final TokenService tokens;
    private final UsuarioRepository usuarios;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            var jwt = tokens.verificar(header.substring(7).trim());
            if (jwt != null) {
                Long id = jwt.getClaim("id").asLong();
                Integer versao = jwt.getClaim("versao").asInt();
                if (id != null && versao != null) usuarios.findById(id)
                        .filter(u -> u.isAtivo() && u.isAcessoHabilitado() && u.getVersaoToken() == versao)
                        .ifPresent(u -> SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(u, null, List.of())));
            }
        }
        chain.doFilter(request, response);
    }
}
