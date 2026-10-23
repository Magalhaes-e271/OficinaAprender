package br.sp.etec.sebrae.OficinaAprender.Security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class LimiteAutenticacaoFilter extends OncePerRequestFilter {
    private final ControleTentativas tentativas;
    public LimiteAutenticacaoFilter(ControleTentativas tentativas) { this.tentativas = tentativas; }
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String rota = req.getRequestURI().substring(req.getContextPath().length());
        boolean mfa = req.getMethod().equals("POST") && rota.startsWith("/auth/mfa/");
        boolean senha = req.getMethod().equals("PUT") && rota.equals("/usuario/senha");
        if ((req.getMethod().equals("POST") && (rota.equals("/login") || rota.equals("/auth/login"))) || mfa || senha) {
            try {
                // Não confia em X-Forwarded-For enviado pelo cliente.
                tentativas.ip(req.getRemoteAddr());
                if (mfa || senha) {
                    var autenticacao = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                    if (autenticacao != null && autenticacao.getPrincipal() instanceof br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario u)
                        tentativas.conta("mfa:" + u.getId());
                }
            } catch (ControleTentativas.LimiteExcedido e) {
                res.setStatus(429); res.setContentType("application/json;charset=UTF-8");
                res.setHeader("Retry-After", String.valueOf(e.segundos)); res.setHeader("Cache-Control", "no-store");
                res.getWriter().write("{\"status\":429,\"message\":\"Muitas tentativas. Aguarde e tente novamente.\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
