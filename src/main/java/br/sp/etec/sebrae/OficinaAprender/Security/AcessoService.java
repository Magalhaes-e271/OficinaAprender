package br.sp.etec.sebrae.OficinaAprender.Security;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.*;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Relacao.RelacaoUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.erro;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AcessoService {
    private final UsuarioRepository usuarios;
    private final RelacaoUsuarioRepository relacoes;
    public Usuario atual() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !(autenticacao.getPrincipal() instanceof Usuario usuario))
            throw erro(UNAUTHORIZED, "Faça login para continuar.");
        return usuario;
    }
    public Usuario profissional() {
        var usuario = atual();
        if (usuario.getTipoUsuario() != TipoUsuario.PROFISSIONAL) throw erro(FORBIDDEN, "Operação disponível para profissionais.");
        return usuario;
    }
    public Usuario usuario(Long id) {
        if (id == null || id <= 0) throw erro(BAD_REQUEST, "Identificador inválido.");
        return usuarios.findById(id).filter(Usuario::isAtivo).orElseThrow(() -> erro(NOT_FOUND, "Usuário não encontrado."));
    }
    public boolean vinculado(Long profissional, Long paciente) {
        return relacoes.vinculoAceito(profissional, paciente, TipoRelacao.PROFISSIONAL_PACIENTE, Solicitacao.ACEITA);
    }
    public Usuario paciente(Long id) {
        var pessoa = usuario(id);
        if (pessoa.getTipoUsuario() != TipoUsuario.PACIENTE) throw erro(BAD_REQUEST, "O usuário informado não é um paciente.");
        var autor = atual();
        if (!autor.getId().equals(id) && !(autor.getTipoUsuario() == TipoUsuario.PROFISSIONAL && vinculado(autor.getId(), id)))
            throw erro(FORBIDDEN, "Você não tem acesso a este paciente.");
        return pessoa;
    }
    public Usuario editarPaciente(Long id) { profissional(); return paciente(id); }
    public void somenteDono(Long id) {
        if (!atual().getId().equals(id)) throw erro(FORBIDDEN, "Você só pode alterar sua própria conta.");
    }
    public void editarPerfil(Long id) {
        if (!atual().getId().equals(id)) editarPaciente(id);
    }
    public void lerPerfil(Long id) {
        var pessoa = usuario(id);
        if (atual().getId().equals(id)) return;
        if (pessoa.getTipoUsuario() == TipoUsuario.PACIENTE) { paciente(id); return; }
        throw erro(FORBIDDEN, "Use a consulta pública de profissionais para visualizar este perfil.");
    }
    public void lerFoto(Long id) {
        var pessoa = usuario(id);
        if (pessoa.getTipoUsuario() == TipoUsuario.PACIENTE) paciente(id);
    }
}
