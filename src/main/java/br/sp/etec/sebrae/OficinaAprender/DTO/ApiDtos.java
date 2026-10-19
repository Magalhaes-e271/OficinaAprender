package br.sp.etec.sebrae.OficinaAprender.DTO;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import br.sp.etec.sebrae.OficinaAprender.Enuns.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/** Contratos de entrada não aceitam autor, dono, datas ou estado de convite do cliente. */
public final class ApiDtos {
    private ApiDtos() {}
    public record Referencia(Long id) {}
    public record PerfilPublico(String nome, String ocupacao, String instituicao, String urlFotoPerfil) {}
    public record Pessoa(Long id, String email, TipoUsuario tipoUsuario, PerfilPublico perfil,
                         String nome, String ocupacao, String instituicao, String urlFotoPerfil) {}
    public record PacienteEntrada(String email, PerfilUsuario perfilUsuario, List<Long> profissionais) {}
    public record RelacaoEntrada(Referencia usuarioDestinatario, TipoRelacao tipoRelacao) {}
    public record RelacaoResposta(Long id, Pessoa usuarioSolicitante, Pessoa usuarioDestinatario,
                                 TipoRelacao tipoRelacao, Solicitacao solicitacao,
                                 Date dataSolicitacao, Date dataAceitacao, Long convidadoPorId) {}
    public record MetaEntrada(String titulo, String area, String conteudoHtml, List<Long> anteriores,
                              Boolean concluido) {}
    public record MetaResposta(Long id, Long idPaciente, String titulo, String area, String conteudoHtml,
                              boolean concluido, Instant dataCriacao, Instant dataAtualizacao,
                              Pessoa responsavel, List<Long> anteriores) {}
    public record PostagemEntrada(String titulo, String conteudoHtml, TipoPostagem tipoPostagem,
                                  Visualizacao visualizacao, Long respondeA) {}
    public record AnexoResposta(Long id, String nome, String urlArquivo, Long tamanhoArquivo,
                                String tipoArquivo) {}
    public record PostagemResposta(Long id, Long idMeta, String titulo, String conteudoHtml,
                                   TipoPostagem tipoPostagem, Visualizacao visualizacao,
                                   Instant dataCriacao, Instant dataAtualizacao, Pessoa autor,
                                   Long respondeA, List<AnexoResposta> anexos) {}
    public record AgendaEntrada(String titulo, Long idPaciente, Instant inicio, Instant fim,
                                String observacoes) {}
    public record AgendaResposta(Long id, String titulo, Long idPaciente, Pessoa paciente,
                                 Instant inicio, Instant fim, String observacoes) {}
    public record ConviteEquipe(Long idProfissional) {}
    public record AtivacaoEntrada(String token, String senha) {}
    public record AtivacaoConsulta(String token) {}
    public record AtivacaoResumo(String nome, String email) {}
    public record AtivacaoResposta(String token, Instant expiraEm, String email) {}
    public record SenhaEntrada(String senhaAtual, String novaSenha) {}
}
