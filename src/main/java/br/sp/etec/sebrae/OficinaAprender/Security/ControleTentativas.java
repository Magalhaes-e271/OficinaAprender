package br.sp.etec.sebrae.OficinaAprender.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/** Contadores compartilhados entre instâncias e preservados após reinício. */
@Service
public class ControleTentativas {
    private final JdbcTemplate jdbc;
    private final int porConta, porIp, janela;
    public ControleTentativas(JdbcTemplate jdbc,
            @Value("${app.auth.account-attempts:${AUTH_ACCOUNT_ATTEMPTS:10}}") int conta,
            @Value("${app.auth.ip-attempts:${AUTH_IP_ATTEMPTS:120}}") int ip,
            @Value("${app.auth.window-seconds:${AUTH_WINDOW_SECONDS:900}}") int janela) {
        if (conta < 1 || ip < 1 || janela < 1 || janela > 86400)
            throw new IllegalArgumentException("Limites de autenticação inválidos.");
        this.jdbc = jdbc; this.porConta = conta; this.porIp = ip; this.janela = janela;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void conta(String identidade) { consumir("conta:" + identidade, porConta); }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void ip(String ip) { consumir("ip:" + ip, porIp); }
    private void consumir(String identidade, int maximo) {
        var r = jdbc.queryForMap("""
            INSERT INTO auth_limite(chave, inicio, tentativas) VALUES (?, clock_timestamp(), 1)
            ON CONFLICT (chave) DO UPDATE SET
              inicio = CASE WHEN auth_limite.inicio <= clock_timestamp() - (? * interval '1 second')
                            THEN clock_timestamp() ELSE auth_limite.inicio END,
              tentativas = CASE WHEN auth_limite.inicio <= clock_timestamp() - (? * interval '1 second')
                                THEN 1 ELSE auth_limite.tentativas + 1 END
            RETURNING tentativas, GREATEST(1, CEIL(EXTRACT(EPOCH FROM
              (inicio + (? * interval '1 second') - clock_timestamp())))) AS espera
            """, hash(identidade), janela, janela, janela);
        if (((Number) r.get("tentativas")).intValue() > maximo)
            throw new LimiteExcedido(((Number) r.get("espera")).intValue());
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sucesso(String identidade) { jdbc.update("DELETE FROM auth_limite WHERE chave = ?", hash("conta:" + identidade)); }
    @Scheduled(fixedDelay = 3600000, initialDelay = 3600000)
    public void limparExpirados() { jdbc.update("DELETE FROM auth_limite WHERE inicio < clock_timestamp() - interval '24 hours'"); }
    public static String hash(String texto) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8))); }
        catch (Exception e) { throw new IllegalStateException(e); }
    }
    public static class LimiteExcedido extends RuntimeException {
        public final int segundos;
        public LimiteExcedido(int segundos) { super("Muitas tentativas. Aguarde e tente novamente."); this.segundos = segundos; }
    }
}
