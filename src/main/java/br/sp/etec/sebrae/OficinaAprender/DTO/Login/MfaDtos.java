package br.sp.etec.sebrae.OficinaAprender.DTO.Login;
import java.time.Instant;
import java.util.List;

public final class MfaDtos {
    private MfaDtos() {}
    public record Status(boolean ativo, boolean disponivel, int codigosRestantes) {}
    public record Senha(String senhaAtual) {}
    public record Codigo(String codigo) {}
    public record Prova(String senhaAtual, String codigo) {}
    public record Preparacao(String chave, String uri, Instant expiraEm) {}
    public record Resultado(LoginResponseDTO sessao, List<String> codigosRecuperacao) {}
    public static class Exigido extends RuntimeException {
        public Exigido() { super("Informe o código do autenticador ou um código de recuperação."); }
    }
}
