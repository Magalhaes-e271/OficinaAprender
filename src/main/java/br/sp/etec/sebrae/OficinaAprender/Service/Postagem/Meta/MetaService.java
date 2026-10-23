package br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Meta.MetaRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.PostagemRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.*;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class MetaService {
    private final EntityManager entidade;
    private final MetaRepository metas;
    private final PostagemRepository postagens;
    private final UsuarioRepository usuarios;
    private final UsuarioService pessoas;
    private final AcessoService acesso;
    @Transactional(readOnly = true)
    public List<MetaResposta> listar(Long paciente) {
        acesso.paciente(paciente);
        var contagens = contagens(paciente);
        return metas.findByPacienteIdOrderByDataCriacaoAsc(paciente).stream().map(m -> resposta(m, contagens)).toList();
    }
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
        var m = new Meta(); m.setPaciente(paciente); m.setResponsavel(acesso.profissional()); m.setAtualizadoPor(acesso.profissional());
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
        entidade.refresh(m);
        conferirVersao(m, dto.versao());
        if (dto.titulo() != null) m.setTitulo(texto(dto.titulo(), "o título", 200));
        if (dto.area() != null) m.setArea(texto(dto.area(), "a área", 200));
        if (dto.conteudoHtml() != null) m.setConteudoHtml(html(dto.conteudoHtml()));
        if (dto.anteriores() != null) m.setAnteriores(dependencias(m, dto.anteriores()));
        if (dto.concluido() != null) concluir(m, dto.concluido());
        else if (m.isConcluido()) concluir(m, true);
        m.setDataAtualizacao(new Date());
        m.setAtualizadoPor(acesso.profissional());
        return resposta(metas.saveAndFlush(m));
    }
    private void conferirVersao(Meta m, Long versao) {
        if (versao != null && !Objects.equals(versao, m.getVersao()))
            throw erro(CONFLICT, "Esta meta foi atualizada por outro profissional. Recarregue a versão atual antes de salvar.");
    }

    /** Altera entradas e saídas em uma única transação, sob o bloqueio do aluno. */
    @Transactional
    public List<MetaResposta> conectar(Long id, ConexoesEntrada dto) {
        var atual = buscar(id);
        var autor = acesso.profissional();
        acesso.editarPaciente(atual.getPaciente().getId());
        usuarios.bloquear(atual.getPaciente().getId()).orElseThrow();
        entidade.refresh(atual);
        if (dto == null || dto.anteriores() == null || dto.seguintes() == null || dto.versoes() == null)
            throw erro(BAD_REQUEST, "Informe as conexões anteriores, seguintes e suas versões.");
        var todas = metas.findByPacienteIdOrderByDataCriacaoAsc(atual.getPaciente().getId());
        var porId = new LinkedHashMap<Long, Meta>();
        todas.forEach(m -> porId.put(m.getId(), m));
        Set<Long> anteriores = validarConexoes(id, dto.anteriores(), porId);
        Set<Long> seguintes = validarConexoes(id, dto.seguintes(), porId);
        var alteradas = new LinkedHashSet<Meta>();
        var afetadas = new LinkedHashSet<Meta>();
        afetadas.add(atual);
        for (var m : todas) {
            if (!m.getId().equals(id) && (seguintes.contains(m.getId()) || m.getAnteriores().stream().anyMatch(a -> a.getId().equals(id))))
                afetadas.add(m);
        }
        for (var m : afetadas) {
            Long versao = dto.versoes().get(m.getId());
            if (versao == null) throw erro(CONFLICT, "O mapa mudou. Atualize as metas antes de alterar as conexões.");
            conferirVersao(m, versao);
        }
        var novosPais = new LinkedHashSet<Meta>();
        anteriores.forEach(a -> novosPais.add(porId.get(a)));
        if (!atual.getAnteriores().equals(novosPais)) {
            atual.setAnteriores(novosPais); alteradas.add(atual);
        }
        for (var m : todas) {
            if (m.getId().equals(id)) continue;
            boolean mudou = seguintes.contains(m.getId()) ? m.getAnteriores().add(atual) : m.getAnteriores().remove(atual);
            if (mudou) alteradas.add(m);
            if (m.getAnteriores().size() > 100) throw erro(BAD_REQUEST, "Use até 100 conexões anteriores por meta.");
        }
        validarGrafo(todas);
        for (var m : todas) {
            if (m.isConcluido() && m.getAnteriores().stream().anyMatch(a -> !a.isConcluido()))
                throw erro(CONFLICT, "Uma meta concluída não pode depender de uma meta em andamento. Reabra a meta antes de conectar.");
        }
        if (!alteradas.isEmpty()) alteradas.add(atual);
        for (var m : alteradas) { m.setDataAtualizacao(new Date()); m.setAtualizadoPor(autor); }
        metas.flush();
        var contagens = contagens(atual.getPaciente().getId());
        return todas.stream().map(m -> resposta(m, contagens)).toList();
    }

    private Set<Long> validarConexoes(Long id, List<Long> ids, Map<Long, Meta> porId) {
        if (ids.size() > 100) throw erro(BAD_REQUEST, "Use até 100 conexões em cada direção.");
        var resultado = new LinkedHashSet<Long>();
        for (Long alvo : ids) {
            if (alvo == null || alvo.equals(id)) throw erro(BAD_REQUEST, "Uma meta não pode ser conectada a si mesma.");
            if (!porId.containsKey(alvo)) throw erro(BAD_REQUEST, "Conecte somente metas deste aluno.");
            resultado.add(alvo);
        }
        return resultado;
    }

    private void validarGrafo(List<Meta> todas) {
        var graus = new HashMap<Long, Integer>();
        var filhos = new HashMap<Long, List<Long>>();
        todas.forEach(m -> { graus.put(m.getId(), m.getAnteriores().size()); filhos.put(m.getId(), new ArrayList<>()); });
        todas.forEach(m -> m.getAnteriores().forEach(a -> filhos.get(a.getId()).add(m.getId())));
        var fila = new ArrayDeque<Long>();
        graus.forEach((id, grau) -> { if (grau == 0) fila.add(id); });
        int visitadas = 0;
        while (!fila.isEmpty()) {
            Long id = fila.remove(); visitadas++;
            for (Long filho : filhos.get(id)) if (graus.merge(filho, -1, Integer::sum) == 0) fila.add(filho);
        }
        if (visitadas != todas.size()) throw erro(BAD_REQUEST, "As conexões formariam um ciclo. Mantenha uma sequência de aprendizagem sem retornar à própria meta.");
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
    private Map<Long, Long> contagens(Long paciente) {
        var resultado = new HashMap<Long, Long>();
        boolean profissional = acesso.atual().getTipoUsuario() == br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario.PROFISSIONAL;
        postagens.contarPorPaciente(paciente, profissional).forEach(linha -> resultado.put((Long) linha[0], (Long) linha[1]));
        return resultado;
    }
    public MetaResposta resposta(Meta m) { return resposta(m, contagens(m.getPaciente().getId())); }
    private MetaResposta resposta(Meta m, Map<Long, Long> contagens) {
        return new MetaResposta(m.getId(), m.getPaciente().getId(), m.getTitulo(), m.getArea(), m.getConteudoHtml(), m.isConcluido(),
                m.getDataCriacao() == null ? null : m.getDataCriacao().toInstant(), m.getDataAtualizacao() == null ? null : m.getDataAtualizacao().toInstant(),
                pessoas.pessoa(m.getResponsavel()), m.getAnteriores().stream().map(Meta::getId).sorted().toList(), m.getVersao(), pessoas.pessoa(m.getAtualizadoPor()), contagens.getOrDefault(m.getId(), 0L));
    }
}
