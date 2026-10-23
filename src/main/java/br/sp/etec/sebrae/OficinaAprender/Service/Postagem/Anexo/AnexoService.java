package br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Anexo;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.AnexoResposta;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Anexo.Anexo;
import br.sp.etec.sebrae.OficinaAprender.Entity.Arquivo.Arquivo;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Anexo.AnexoRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.PostagemService;
import br.sp.etec.sebrae.OficinaAprender.Service.Arquivo.ArquivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;
@Service
@RequiredArgsConstructor
public class AnexoService {
    private final AnexoRepository anexos;
    private final PostagemService posts;
    private final ArquivoService arquivos;
    private final UsuarioRepository usuarios;
    @Transactional
    public AnexoResposta criar(MultipartFile arquivo, Long idPostagem) {
        var p = posts.buscar(idPostagem); posts.autor(p);
        usuarios.bloquear(p.getMeta().getPaciente().getId()).orElseThrow();
        arquivos.validarLote(List.of(arquivo));
        long soma = anexos.findByPostagemId(idPostagem).stream().mapToLong(a -> a.getTamanhoArquivo() == null ? 0 : a.getTamanhoArquivo()).sum();
        if (soma + arquivo.getSize() > ArquivoService.TOTAL || anexos.findByPostagemId(idPostagem).size() >= 10)
            throw erro(BAD_REQUEST, "A publicação deve ter até 10 arquivos e até 11 MB no total.");
        return posts.anexo(posts.anexar(p, arquivo));
    }
    private Anexo buscar(Long id) {
        var a = anexos.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Anexo não encontrado."));
        if (a.getPostagem() == null) throw erro(CONFLICT, "Anexo sem postagem associada.");
        posts.buscar(a.getPostagem().getId()); return a;
    }
    @Transactional(readOnly = true) public AnexoResposta findByID(Long id) { return posts.anexo(buscar(id)); }
    @Transactional(readOnly = true) public List<AnexoResposta> buscarPorPostagem(Long id) { posts.buscar(id); return anexos.findByPostagemId(id).stream().map(posts::anexo).toList(); }
    @Transactional(readOnly = true) public Arquivo arquivo(Long id) { return arquivos.buscar(buscar(id).getArquivoId()); }
    @Transactional public AnexoResposta editar(Long id, String nome) {
        var a = buscar(id); posts.autor(a.getPostagem()); a.setNome(arquivos.nome(texto(nome, "o nome", 200))); return posts.anexo(anexos.save(a));
    }
    @Transactional public void deletar(Long id) {
        var a = buscar(id); posts.autor(a.getPostagem()); Long arquivo = a.getArquivoId();
        anexos.delete(a); anexos.flush(); arquivos.remover(arquivo);
    }
}
