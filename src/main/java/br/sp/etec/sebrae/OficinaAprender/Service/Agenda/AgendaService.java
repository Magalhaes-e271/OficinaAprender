package br.sp.etec.sebrae.OficinaAprender.Service.Agenda;
import br.sp.etec.sebrae.OficinaAprender.DTO.ApiDtos.*;
import br.sp.etec.sebrae.OficinaAprender.Entity.Agenda.Compromisso;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Agenda.CompromissoRepository;
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
public class AgendaService {
    private final CompromissoRepository agenda;
    private final UsuarioRepository usuarios;
    private final UsuarioService pessoas;
    private final AcessoService acesso;
    @Transactional(readOnly = true)
    public List<AgendaResposta> listar() { return agenda.findByProfissionalIdOrderByInicioAsc(acesso.profissional().getId()).stream().map(this::resposta).toList(); }
    @Transactional
    public AgendaResposta criar(AgendaEntrada dto) {
        var profissional = acesso.profissional();
        if (dto.inicio() == null || dto.fim() == null || !dto.fim().isAfter(dto.inicio())) throw erro(BAD_REQUEST, "O fim deve ser depois do início.");
        if (java.time.Duration.between(dto.inicio(), dto.fim()).toHours() > 24) throw erro(BAD_REQUEST, "O atendimento deve ter até 24 horas.");
        Usuario paciente = dto.idPaciente() == null ? null : acesso.editarPaciente(dto.idPaciente());
        // Bloqueios no banco, em ordem estável, protegem requisições concorrentes em várias instâncias.
        var ids = new TreeSet<Long>(); ids.add(profissional.getId()); if (paciente != null) ids.add(paciente.getId());
        ids.forEach(id -> usuarios.bloquear(id).orElseThrow());
        if (agenda.conflito(profissional.getId(), dto.idPaciente(), dto.inicio(), dto.fim())) throw erro(CONFLICT, "O profissional ou paciente já tem atendimento neste intervalo.");
        var c = new Compromisso(); c.setProfissional(profissional); c.setPaciente(paciente);
        c.setTitulo(texto(dto.titulo(), "o título", 200)); c.setInicio(dto.inicio()); c.setFim(dto.fim()); c.setObservacoes(opcional(dto.observacoes(), "As observações", 10000));
        return resposta(agenda.saveAndFlush(c));
    }
    @Transactional
    public void remover(Long id) {
        var profissional = acesso.profissional();
        var c = agenda.findById(id).orElseThrow(() -> erro(NOT_FOUND, "Atendimento não encontrado."));
        if (!c.getProfissional().getId().equals(profissional.getId())) throw erro(FORBIDDEN, "Você só pode cancelar seus próprios atendimentos.");
        usuarios.bloquear(profissional.getId()).orElseThrow(); agenda.delete(c);
    }
    private AgendaResposta resposta(Compromisso c) {
        return new AgendaResposta(c.getId(), c.getTitulo(), c.getPaciente() == null ? null : c.getPaciente().getId(),
                pessoas.pessoa(c.getPaciente()), c.getInicio(), c.getFim(), c.getObservacoes());
    }
}
