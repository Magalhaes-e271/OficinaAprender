package br.sp.etec.sebrae.OficinaAprender.Service.Usuario;

import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.DTO.Login.LoginResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.Usuario.UsuarioCriarDTO;
import br.sp.etec.sebrae.OficinaAprender.DTO.UsuarioResponseDTO;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Entity.Arquivo.Arquivo;
import br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.PerfilUsuario.PerfilUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Arquivo.ArquivoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final PerfilUsuarioRepository perfis;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final AcessoService acesso;
    private final ArquivoService arquivos;
    @Transactional
    public LoginResponseDTO criar(UsuarioCriarDTO dto, MultipartFile foto) {
        if (dto == null || dto.tipoUsuario() == null) throw erro(BAD_REQUEST, "Informe os dados do cadastro.");
        String email = email(dto.email()); senha(dto.senha());
        perfil(dto.perfilUsuario(), dto.tipoUsuario() == TipoUsuario.PROFISSIONAL);
        if (usuarios.findByEmail(email).isPresent()) throw erro(CONFLICT, "Já existe um usuário com esse e-mail.");
        var imagem = arquivos.salvar(foto, true);
        var u = new Usuario(); u.setEmail(email); u.setSenha(encoder.encode(dto.senha())); u.setTipoUsuario(dto.tipoUsuario());
        u = usuarios.saveAndFlush(u);
        // Copiar somente os campos editáveis: IDs e caminhos recebidos são ignorados.
        var p = new PerfilUsuario(); copiar(dto.perfilUsuario(), p);
        p.setUsuario(u); p.setFotoId(imagem.getId()); p.setUrlFotoPerfil("/usuario/foto?id=" + u.getId());
        perfis.saveAndFlush(p);
        return new LoginResponseDTO(tokens.gerarToken(u), converterParaDTO(u));
    }
    public UsuarioResponseDTO buscarSessao(String ignorado) { return converterParaDTO(acesso.atual()); }
    public UsuarioResponseDTO findById(Long id, String ignorado) { acesso.lerPerfil(id); return converterParaDTO(acesso.usuario(id)); }
    public UsuarioResponseDTO findByEmail(String email, String ignorado) {
        var u = usuarios.findByEmail(email(email)).orElseThrow(() -> erro(NOT_FOUND, "Usuário não encontrado."));
        return findById(u.getId(), ignorado);
    }
    @Transactional
    public UsuarioResponseDTO editar(Long id, String novoEmail) {
        acesso.somenteDono(id);
        var u = acesso.usuario(id);
        if (novoEmail != null) {
            String normalizado = email(novoEmail);
            usuarios.findByEmail(normalizado).filter(outro -> !outro.getId().equals(id)).ifPresent(outro -> { throw erro(CONFLICT, "Já existe um usuário com esse e-mail."); });
            u.setEmail(normalizado);
        }
        return converterParaDTO(usuarios.save(u));
    }
    @Transactional
    public void alterarSenha(SenhaEntrada dto) {
        var u = acesso.usuario(acesso.atual().getId());
        if (dto == null || dto.senhaAtual() == null || !encoder.matches(dto.senhaAtual(), u.getSenha()))
            throw erro(UNAUTHORIZED, "A senha atual está incorreta.");
        senha(dto.novaSenha()); u.setSenha(encoder.encode(dto.novaSenha())); u.setVersaoToken(u.getVersaoToken() + 1);
        usuarios.save(u);
    }
    @Transactional
    public void deletar(Long id, String ignorado) {
        acesso.somenteDono(id);
        var u = acesso.usuario(id);
        // Preserva a autoria dos registros clínicos e revoga imediatamente todas as sessões.
        u.setAtivo(false); u.setAcessoHabilitado(false); u.setVersaoToken(u.getVersaoToken() + 1);
        u.setEmail("conta-encerrada-" + id + "@invalid.local");
        u.setSenha(encoder.encode(java.util.UUID.randomUUID().toString()));
        var p = perfis.findById(id).orElse(null);
        if (p != null) {
            Long foto = p.getFotoId(); p.setFotoId(null); p.setFotoArquivo(null); p.setUrlFotoPerfil(null);
            p.setNome("Conta encerrada"); p.setTelefone(null); p.setCpf(null); p.setDataNascimento(null);
            p.setOcupacao(null); p.setInstituicao(null); p.setCondicao(null); p.setConteudoHtml("");
            perfis.saveAndFlush(p); arquivos.remover(foto);
        }
        usuarios.save(u);
    }
    @Transactional
    public PerfilUsuario trocarFoto(Long id, MultipartFile foto) {
        acesso.editarPerfil(id);
        var p = perfis.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Perfil não encontrado."));
        var imagem = arquivos.salvar(foto, true);
        Long anterior = p.getFotoId();
        p.setFotoId(imagem.getId()); p.setFotoArquivo(null); p.setUrlFotoPerfil("/usuario/foto?id=" + id);
        perfis.saveAndFlush(p); arquivos.remover(anterior);
        return p;
    }
    public Arquivo buscarFoto(Long id) {
        acesso.lerFoto(id);
        var p = perfis.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Perfil não encontrado."));
        return arquivos.buscar(p.getFotoId());
    }
    public List<Pessoa> buscarProfissionais(String nome) {
        acesso.profissional();
        String termo = opcional(nome, "A busca", 100);
        if (termo == null) termo = "";
        return usuarios.buscarProfissionais("%" + termo.replace("%", "").replace("_", "") + "%", PageRequest.of(0, 50))
                .stream().filter(u -> !u.getId().equals(acesso.atual().getId())).map(this::pessoa).toList();
    }
    public Pessoa pessoa(Usuario u) {
        if (u == null) return null;
        var p = perfis.findById(u.getId()).orElse(null);
        String nome = p == null ? "Usuário" : p.getNome();
        String ocupacao = p == null ? null : p.getOcupacao();
        String instituicao = p == null ? null : p.getInstituicao();
        String foto = p == null ? null : p.getUrlFotoPerfil();
        return new Pessoa(u.getId(), u.isAtivo() ? u.getEmail() : null, u.getTipoUsuario(),
                new PerfilPublico(nome, ocupacao, instituicao, foto), nome, ocupacao, instituicao, foto);
    }
    public UsuarioResponseDTO converterParaDTO(Usuario usuario) { return converterParaDTO(usuario, null); }
    public UsuarioResponseDTO converterParaDTO(Usuario usuario, String token) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getEmail(), usuario.getTipoUsuario(),
                perfis.findById(usuario.getId()).orElse(null), token);
    }
    public static void copiar(PerfilUsuario origem, PerfilUsuario destino) {
        destino.setNome(origem.getNome()); destino.setTelefone(origem.getTelefone()); destino.setCpf(origem.getCpf());
        destino.setDataNascimento(origem.getDataNascimento()); destino.setOcupacao(origem.getOcupacao());
        destino.setInstituicao(origem.getInstituicao()); destino.setConteudoHtml(origem.getConteudoHtml());
        destino.setHorarioDeEntrada(origem.getHorarioDeEntrada()); destino.setHorarioDeSaida(origem.getHorarioDeSaida());
        destino.setCondicao(origem.getCondicao());
    }
}
