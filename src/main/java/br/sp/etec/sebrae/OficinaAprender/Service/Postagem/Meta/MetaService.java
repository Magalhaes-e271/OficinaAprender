package br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Meta.MetaRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.PostagemRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class MetaService {
    private final MetaRepository metas;
    private final PostagemRepository postagens;
    private final UsuarioRepository usuarios;
    private final UsuarioService pessoas;
    private final AcessoService acesso;
    @Transactional(readOnly = true)
    public List<MetaResposta> listar(Long paciente) { acesso.paciente(paciente); return metas.findByPacienteIdOrderByDataCriacaoAsc(paciente).stream().map(this::resposta).toList(); }
    @Transactional(readOnly = true)
    public Meta buscar(Long id) {
        var m = metas.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Meta não encontrada."));
        if (m.getPaciente() == null) throw erro(CONFLICT, "A meta antiga precisa ser associada a um paciente.");
        acesso.paciente(m.getPaciente().getId()); return m;
    }
    @Transactional(readOnly = true)
    public MetaResposta detalhar(Long id) { return resposta(buscar(id)); }
    @Transactional
    public MetaResposta criar(Long idPaciente, MetaEntrada dto) {
        var paciente = acesso.editarPaciente(idPaciente);
        usuarios.bloquear(paciente.getId()).orElseThrow();
        var m = new Meta(); m.setPaciente(paciente); m.setResponsavel(acesso.profissional());
        m.setTitulo(texto(dto.titulo(), "o título da meta", 200)); m.setArea(texto(dto.area(), "a área", 200));
        m.setConteudoHtml(html(dto.conteudoHtml())); m.setDataCriacao(new Date()); m.setDataAtualizacao(new Date());
        m.setAnteriores(dependencias(m, dto.anteriores()));
        if (Boolean.TRUE.equals(dto.concluido())) concluir(m, true);
        return resposta(metas.saveAndFlush(m));
    }
    @Transactional
    public MetaResposta editar(Long id, MetaEntrada dto) {
        var m = buscar(id); acesso.editarPaciente(m.getPaciente().getId());
        usuarios.bloquear(m.getPaciente().getId()).orElseThrow();
        if (dto.titulo() != null) m.setTitulo(texto(dto.titulo(), "o título", 200));
        if (dto.area() != null) m.setArea(texto(dto.area(), "a área", 200));
        if (dto.conteudoHtml() != null) m.setConteudoHtml(html(dto.conteudoHtml()));
        if (dto.anteriores() != null) m.setAnteriores(dependencias(m, dto.anteriores()));
        if (dto.concluido() != null) concluir(m, dto.concluido());
        else if (m.isConcluido()) concluir(m, true);
        m.setDataAtualizacao(new Date());
        return resposta(metas.saveAndFlush(m));
    }
    private Set<Meta> dependencias(Meta atual, List<Long> ids) {
        Set<Meta> lista = new LinkedHashSet<>();
        if (ids == null) return lista;
        if (ids.size() > 100) throw erro(BAD_REQUEST, "Use até 100 dependências por meta.");
        for (Long id : new LinkedHashSet<>(ids)) {
            if (id == null || Objects.equals(id, atual.getId())) throw erro(BAD_REQUEST, "A meta não pode depender de si mesma.");
            var anterior = buscar(id);
            if (!anterior.getPaciente().getId().equals(atual.getPaciente().getId())) throw erro(BAD_REQUEST, "As dependências devem pertencer ao mesmo paciente.");
            if (alcança(anterior, atual.getId(), new HashSet<>())) throw erro(BAD_REQUEST, "As dependências não podem formar um ciclo.");
            lista.add(anterior);
        }
        return lista;
    }
    private boolean alcança(Meta meta, Long alvo, Set<Long> vistos) {
        if (Objects.equals(meta.getId(), alvo)) return true;
        if (!vistos.add(meta.getId())) return false;
        return meta.getAnteriores().stream().anyMatch(m -> alcança(m, alvo, vistos));
    }
    private void concluir(Meta m, boolean concluido) {
        if (concluido && m.getAnteriores().stream().anyMatch(a -> !a.isConcluido())) throw erro(CONFLICT, "Conclua as metas anteriores primeiro.");
        if (!concluido && metas.findByPacienteIdOrderByDataCriacaoAsc(m.getPaciente().getId()).stream()
                .anyMatch(outra -> !Objects.equals(outra.getId(), m.getId()) && outra.isConcluido() && alcança(outra, m.getId(), new HashSet<>())))
            throw erro(CONFLICT, "Reabra primeiro as metas concluídas que dependem desta.");
        m.setConcluido(concluido);
    }
    @Transactional
    public void remover(Long id) {
        var m = buscar(id); acesso.editarPaciente(m.getPaciente().getId()); usuarios.bloquear(m.getPaciente().getId()).orElseThrow();
        if (!postagens.findByMetaIdOrderByDataCriacaoAscIdAsc(id).isEmpty() || metas.findByPacienteIdOrderByDataCriacaoAsc(m.getPaciente().getId()).stream()
                .anyMatch(outra -> outra.getAnteriores().stream().anyMatch(a -> a.getId().equals(id))))
            throw erro(CONFLICT, "A meta tem postagens ou dependências e não pode ser removida.");
        metas.delete(m);
    }
    public MetaResposta resposta(Meta m) {
        return new MetaResposta(m.getId(), m.getPaciente().getId(), m.getTitulo(), m.getArea(), m.getConteudoHtml(), m.isConcluido(),
                m.getDataCriacao() == null ? null : m.getDataCriacao().toInstant(), m.getDataAtualizacao() == null ? null : m.getDataAtualizacao().toInstant(),
                pessoas.pessoa(m.getResponsavel()), m.getAnteriores().stream().map(Meta::getId).sorted().toList());
    }
}
