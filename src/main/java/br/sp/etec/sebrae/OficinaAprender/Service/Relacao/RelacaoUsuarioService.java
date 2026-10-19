package br.sp.etec.sebrae.OficinaAprender.Service.Relacao;

import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.*;
import br.sp.etec.sebrae.OficinaAprender.Repository.Relacao.RelacaoUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.UsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Security.AcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.erro;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class RelacaoUsuarioService {
    private final RelacaoUsuarioRepository relacoes;
    private final UsuarioRepository usuarios;
    private final UsuarioService pessoas;
    private final AcessoService acesso;
    @Transactional(readOnly = true)
    public List<RelacaoResposta> listar(TipoRelacao tipo) {
        return relacoes.participantes(acesso.atual().getId()).stream()
                .filter(r -> tipo == null || r.getTipoRelacao() == tipo).map(this::resposta).toList();
    }
    @Transactional
    public RelacaoResposta criar(RelacaoEntrada dto) {
        var autor = acesso.profissional();
        if (dto == null || dto.usuarioDestinatario() == null) throw erro(BAD_REQUEST, "Informe o destinatário.");
        var destino = acesso.usuario(dto.usuarioDestinatario().id());
        TipoRelacao tipo = destino.getTipoUsuario() == TipoUsuario.PACIENTE ? TipoRelacao.PROFISSIONAL_PACIENTE : TipoRelacao.PROFISSIONAL_PROFISSIONAL;
        if (dto.tipoRelacao() != null && dto.tipoRelacao() != tipo) throw erro(BAD_REQUEST, "Tipo de vínculo incompatível com os usuários.");
        return resposta(vincular(autor, destino, tipo, Solicitacao.PRENDENTE, autor.getId()));
    }
    /** Usado em uma transação maior pelo cadastro de pacientes. */
    public RelacaoUsuario vincular(Usuario origem, Usuario destino, TipoRelacao tipo, Solicitacao estado, Long convidadoPor) {
        if (origem.getId().equals(destino.getId())) throw erro(BAD_REQUEST, "Não é possível criar um vínculo consigo mesmo.");
        bloquear(origem.getId(), destino.getId());
        if (!relacoes.entre(origem.getId(), destino.getId()).isEmpty()) throw erro(CONFLICT, "Já existe um vínculo ou solicitação entre estes usuários.");
        var r = new RelacaoUsuario(); r.setUsuarioSolicitante(origem); r.setUsuarioDestinatario(destino);
        r.setTipoRelacao(tipo); r.setSolicitacao(estado); r.setDataSolicitacao(new Date()); r.setConvidadoPorId(convidadoPor);
        if (estado == Solicitacao.ACEITA) r.setDataAceitacao(new Date());
        return relacoes.saveAndFlush(r);
    }
    private void bloquear(Long a, Long b) {
        java.util.stream.Stream.of(a,b).distinct().sorted().forEach(id -> usuarios.bloquear(id).orElseThrow());
    }
    @Transactional
    public RelacaoResposta aceitar(Long id) {
        var r = buscar(id);
        if (!r.getUsuarioDestinatario().getId().equals(acesso.atual().getId())) throw erro(FORBIDDEN, "Somente o destinatário pode aceitar esta solicitação.");
        if (!r.getUsuarioSolicitante().isAtivo()) throw erro(CONFLICT, "A conta que solicitou o vínculo foi encerrada.");
        if (r.getSolicitacao() == Solicitacao.ACEITA) return resposta(r);
        r.setSolicitacao(Solicitacao.ACEITA); r.setDataAceitacao(new Date());
        return resposta(relacoes.save(r));
    }
    @Transactional
    public void remover(Long id) {
        var r = buscar(id); Long autor = acesso.atual().getId();
        boolean participante = r.getUsuarioSolicitante().getId().equals(autor) || r.getUsuarioDestinatario().getId().equals(autor);
        boolean criadorConvite = Objects.equals(r.getConvidadoPorId(), autor) && r.getSolicitacao() != Solicitacao.ACEITA;
        boolean gestor = false;
        if (r.getTipoRelacao() == TipoRelacao.PROFISSIONAL_PACIENTE) {
            var paciente = r.getUsuarioSolicitante().getTipoUsuario() == TipoUsuario.PACIENTE ? r.getUsuarioSolicitante() : r.getUsuarioDestinatario();
            gestor = Objects.equals(paciente.getCriadoPorId(), autor) && acesso.vinculado(autor, paciente.getId());
        }
        if (!participante && !criadorConvite && !gestor) throw erro(FORBIDDEN, "Você não pode remover este vínculo.");
        relacoes.delete(r);
    }
    private RelacaoUsuario buscar(Long id) { return relacoes.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Vínculo não encontrado.")); }
    public RelacaoResposta resposta(RelacaoUsuario r) {
        return new RelacaoResposta(r.getId(), pessoas.pessoa(r.getUsuarioSolicitante()), pessoas.pessoa(r.getUsuarioDestinatario()),
                r.getTipoRelacao(), r.getSolicitacao(), r.getDataSolicitacao(), r.getDataAceitacao(), r.getConvidadoPorId());
    }
}
