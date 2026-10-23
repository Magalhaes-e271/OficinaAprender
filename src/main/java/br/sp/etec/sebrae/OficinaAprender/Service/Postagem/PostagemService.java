package br.sp.etec.sebrae.OficinaAprender.Service.Postagem;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Enuns.*;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.PostagemRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Anexo.AnexoRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Arquivo.ArquivoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta.MetaService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.*;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class PostagemService {
    private final PostagemRepository postagens;
    private final AnexoRepository anexos;
    private final UsuarioRepository usuarios;
    private final MetaService metas;
    private final AcessoService acesso;
    private final UsuarioService pessoas;
    private final ArquivoService arquivos;
    @Transactional(readOnly = true)
    public List<PostagemResposta> listar(Long meta) {
        metas.buscar(meta);
        return postagens.findByMetaIdOrderByDataCriacaoAscIdAsc(meta).stream().filter(this::podeLer).map(this::resposta).toList();
    }
    public boolean podeLer(Postagem p) {
        return p.getVisualizacao() != Visualizacao.PROFISSIONAIS || acesso.atual().getTipoUsuario() == TipoUsuario.PROFISSIONAL;
    }
    @Transactional(readOnly = true)
    public Postagem buscar(Long id) {
        var p = postagens.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Postagem não encontrada."));
        if (p.getMeta() == null) throw erro(CONFLICT, "Postagem antiga sem meta associada.");
        metas.buscar(p.getMeta().getId());
        if (!podeLer(p)) throw erro(FORBIDDEN, "Esta postagem é restrita aos profissionais da equipe.");
        return p;
    }
    public void autor(Postagem p) {
        if (p.getAutor() == null || !p.getAutor().getId().equals(acesso.atual().getId())) throw erro(FORBIDDEN, "Somente o autor pode alterar esta postagem.");
    }
    @Transactional
    public PostagemResposta criar(Long idMeta, PostagemEntrada dto, List<MultipartFile> lote) {
        var meta = metas.buscar(idMeta); var u = acesso.atual();
        usuarios.bloquear(meta.getPaciente().getId()).orElseThrow();
        arquivos.validarLote(lote);
        var p = new Postagem(); p.setMeta(meta); p.setAutor(u);
        p.setTitulo(texto(dto.titulo(), "o título da postagem", 200));
        String conteudo = html(dto.conteudoHtml());
        if (org.jsoup.Jsoup.parse(conteudo).text().isBlank()) throw erro(BAD_REQUEST, "Escreva o conteúdo da postagem.");
        p.setConteudoHtml(conteudo);
        if (dto.tipoPostagem() == null) throw erro(BAD_REQUEST, "Informe o tipo de postagem.");
        p.setTipoPostagem(dto.tipoPostagem());
        p.setVisualizacao(dto.visualizacao() == null ? Visualizacao.TODOS : dto.visualizacao());
        if (u.getTipoUsuario() == TipoUsuario.PACIENTE && p.getVisualizacao() == Visualizacao.PROFISSIONAIS)
            throw erro(FORBIDDEN, "O paciente não pode publicar conteúdo restrito aos profissionais.");
        if (dto.respondeA() != null) {
            var pai = buscar(dto.respondeA());
            if (!pai.getMeta().getId().equals(idMeta)) throw erro(BAD_REQUEST, "Responda a uma postagem da mesma meta.");
            if (pai.getVisualizacao() == Visualizacao.PROFISSIONAIS && p.getVisualizacao() != Visualizacao.PROFISSIONAIS)
                throw erro(BAD_REQUEST, "A resposta deve manter a visibilidade restrita da postagem original.");
            p.setRespondeA(pai);
        }
        p.setDataCriacao(LocalDateTime.now(ZoneOffset.UTC)); p.setDataAtualizacao(p.getDataCriacao());
        p = postagens.saveAndFlush(p);
        if (lote != null) for (MultipartFile arquivo : lote) anexar(p, arquivo);
        return resposta(p);
    }
    public Anexo anexar(Postagem p, MultipartFile arquivo) {
        var dados = arquivos.salvar(arquivo, false);
        var a = new Anexo(); a.setPostagem(p); a.setArquivoId(dados.getId()); a.setNome(dados.getNome());
        a.setTamanhoArquivo(dados.getTamanho()); a.setTipoArquivo(dados.getTipo()); a.setDataUpload(LocalDateTime.now(ZoneOffset.UTC));
        a = anexos.saveAndFlush(a); a.setUrlArquivo("/anexo/" + a.getId() + "/arquivo");
        return anexos.save(a);
    }
    @Transactional
    public PostagemResposta editar(Long id, PostagemEntrada dto) {
        var p = buscar(id); autor(p);
        if (dto.titulo() != null) p.setTitulo(texto(dto.titulo(), "o título", 200));
        if (dto.conteudoHtml() != null) {
            String conteudo = html(dto.conteudoHtml());
            if (org.jsoup.Jsoup.parse(conteudo).text().isBlank()) throw erro(BAD_REQUEST, "Escreva o conteúdo da postagem.");
            p.setConteudoHtml(conteudo);
        }
        if (dto.tipoPostagem() != null) p.setTipoPostagem(dto.tipoPostagem());
        // Alterar visibilidade depois das respostas pode revelar conteúdo de terceiros.
        if (dto.visualizacao() != null && dto.visualizacao() != p.getVisualizacao()) throw erro(CONFLICT, "A visibilidade não pode ser alterada após publicar.");
        p.setDataAtualizacao(LocalDateTime.now(ZoneOffset.UTC)); return resposta(postagens.save(p));
    }
    @Transactional
    public void remover(Long id) {
        var p = buscar(id); autor(p); usuarios.bloquear(p.getMeta().getPaciente().getId()).orElseThrow();
        if (postagens.existsByRespondeAId(id)) throw erro(CONFLICT, "A postagem já recebeu respostas e não pode ser removida.");
        var lista = anexos.findByPostagemId(id);
        var ids = lista.stream().map(Anexo::getArquivoId).filter(Objects::nonNull).toList();
        anexos.deleteAll(lista); anexos.flush(); postagens.delete(p); postagens.flush(); ids.forEach(arquivos::remover);
    }
    public AnexoResposta anexo(Anexo a) { return new AnexoResposta(a.getId(), a.getNome(), "/anexo/" + a.getId() + "/arquivo", a.getTamanhoArquivo(), a.getTipoArquivo()); }
    public PostagemResposta resposta(Postagem p) {
        return new PostagemResposta(p.getId(), p.getMeta().getId(), p.getTitulo(), p.getConteudoHtml(), p.getTipoPostagem(), p.getVisualizacao(),
                p.getDataCriacao() == null ? null : p.getDataCriacao().toInstant(ZoneOffset.UTC), p.getDataAtualizacao() == null ? null : p.getDataAtualizacao().toInstant(ZoneOffset.UTC),
                pessoas.pessoa(p.getAutor()), p.getRespondeA() == null ? null : p.getRespondeA().getId(), anexos.findByPostagemId(p.getId()).stream().map(this::anexo).toList());
    }
}
