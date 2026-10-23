package br.sp.etec.sebrae.OficinaAprender.Security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;

/** RFC 6238: HMAC-SHA1, 30 segundos, seis dígitos e tolerância de um intervalo. */
public final class Totp {
    private static final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private Totp() {}
    public static String novoSegredo() {
        byte[] bytes = new byte[20]; new SecureRandom().nextBytes(bytes);
        var texto = new StringBuilder(); int buffer = 0, bits = 0;
        for (byte b : bytes) { buffer = (buffer << 8) | (b & 255); bits += 8;
            while (bits >= 5) { bits -= 5; texto.append(ALFABETO.charAt((buffer >> bits) & 31)); }
        }
        return texto.toString();
    }
    static byte[] decodificar(String texto) {
        var bytes = new java.io.ByteArrayOutputStream(); int buffer = 0, bits = 0;
        for (char c : texto.toUpperCase(Locale.ROOT).toCharArray()) {
            int n = ALFABETO.indexOf(c); if (n < 0) throw new IllegalArgumentException("Chave TOTP inválida.");
            buffer = (buffer << 5) | n; bits += 5;
            if (bits >= 8) { bits -= 8; bytes.write((buffer >> bits) & 255); }
        }
        return bytes.toByteArray();
    }
    public static String codigo(String segredo, long passo, int digitos) {
        try {
            var mac = Mac.getInstance("HmacSHA1"); mac.init(new SecretKeySpec(decodificar(segredo), "HmacSHA1"));
            byte[] hash = mac.doFinal(ByteBuffer.allocate(8).putLong(passo).array());
            int deslocamento = hash[hash.length - 1] & 15;
            int valor = ByteBuffer.wrap(hash, deslocamento, 4).getInt() & 0x7fffffff;
            int modulo = digitos == 8 ? 100000000 : 1000000;
            return String.format(Locale.ROOT, "%0" + digitos + "d", valor % modulo);
        } catch (Exception e) { throw new IllegalStateException("Não foi possível verificar o autenticador.", e); }
    }
    public static long verificar(String segredo, String codigo, Instant agora, long ultimo) {
        if (codigo == null || !codigo.matches("[0-9]{6}")) return -1;
        long atual = agora.getEpochSecond() / 30, aceito = -1;
        for (long n = atual - 1; n <= atual + 1; n++) {
            boolean confere = MessageDigest.isEqual(codigo(segredo, n, 6).getBytes(StandardCharsets.US_ASCII), codigo.getBytes(StandardCharsets.US_ASCII));
            if (confere && n > ultimo) aceito = n;
        }
        return aceito;
    }
}
