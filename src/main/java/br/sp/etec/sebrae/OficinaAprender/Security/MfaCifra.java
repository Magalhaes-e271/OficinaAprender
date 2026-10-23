package br.sp.etec.sebrae.OficinaAprender.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;
import static br.sp.etec.sebrae.OficinaAprender.Service.Validacao.erro;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Component
public class MfaCifra {
    private final byte[] chave;
    public MfaCifra(@Value("${app.mfa.encryption-key:${MFA_ENCRYPTION_KEY:}}") String valor) {
        try { chave = valor.isBlank() ? null : Base64.getDecoder().decode(valor.trim()); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("MFA_ENCRYPTION_KEY deve ser uma chave Base64 de 32 bytes."); }
        if (chave != null && chave.length != 32) throw new IllegalArgumentException("MFA_ENCRYPTION_KEY deve conter 32 bytes.");
    }
    public boolean disponivel() { return chave != null; }
    public String cifrar(String segredo, long usuario) {
        exigirChave();
        try {
            byte[] iv = new byte[12]; new SecureRandom().nextBytes(iv);
            var c = cifra(Cipher.ENCRYPT_MODE, iv, usuario); byte[] corpo = c.doFinal(segredo.getBytes(StandardCharsets.UTF_8));
            byte[] pacote = new byte[iv.length + corpo.length]; System.arraycopy(iv, 0, pacote, 0, iv.length); System.arraycopy(corpo, 0, pacote, iv.length, corpo.length);
            return Base64.getEncoder().encodeToString(pacote);
        } catch (Exception e) { throw erro(SERVICE_UNAVAILABLE, "Não foi possível proteger a configuração do autenticador."); }
    }
    public String abrir(String valor, long usuario) {
        exigirChave();
        try { byte[] pacote = Base64.getDecoder().decode(valor);
            return new String(cifra(Cipher.DECRYPT_MODE, Arrays.copyOfRange(pacote, 0, 12), usuario).doFinal(Arrays.copyOfRange(pacote, 12, pacote.length)), StandardCharsets.UTF_8);
        } catch (Exception e) { throw erro(SERVICE_UNAVAILABLE, "Configuração do autenticador indisponível. Contate o responsável pelo servidor."); }
    }
    private Cipher cifra(int modo, byte[] iv, long usuario) throws Exception {
        var c = Cipher.getInstance("AES/GCM/NoPadding"); c.init(modo, new SecretKeySpec(chave, "AES"), new GCMParameterSpec(128, iv));
        c.updateAAD(("oficina:mfa:" + usuario).getBytes(StandardCharsets.UTF_8)); return c;
    }
    private void exigirChave() { if (!disponivel()) throw erro(SERVICE_UNAVAILABLE, "A configuração do autenticador ainda não está disponível neste servidor."); }
}
