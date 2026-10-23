package br.sp.etec.sebrae.OficinaAprender.Security;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class AutenticacaoTests {
    static final String CHAVE = Base64.getEncoder().encodeToString(new byte[32]);
    @Test void vetoresOficiaisRfc6238() {
        String segredo = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ";
        long[] segundos = {59,1111111109,1111111111,1234567890,2000000000,20000000000L};
        String[] esperados = {"94287082","07081804","14050471","89005924","69279037","65353130"};
        for (int i=0;i<segundos.length;i++) assertEquals(esperados[i],Totp.codigo(segredo,segundos[i]/30,8));
    }
    @Test void codigoNaoPodeSerReutilizadoEExpiraForaDaJanela() {
        String segredo=Totp.novoSegredo(); Instant agora=Instant.ofEpochSecond(2000000010); long passo=agora.getEpochSecond()/30;
        String codigo=Totp.codigo(segredo,passo,6);
        assertEquals(passo,Totp.verificar(segredo,codigo,agora,-1));
        assertEquals(-1,Totp.verificar(segredo,codigo,agora,passo));
        assertEquals(-1,Totp.verificar(segredo,codigo,agora.plusSeconds(90),-1));
        assertEquals(-1,Totp.verificar(segredo,"12345x",agora,-1));
        assertEquals(passo+1,Totp.verificar(segredo,Totp.codigo(segredo,passo+1,6),agora,-1));
    }
    @Test void cifraAutenticadaVinculadaAoUsuario() {
        var c=new MfaCifra(CHAVE);String segredo=Totp.novoSegredo();String a=c.cifrar(segredo,1),b=c.cifrar(segredo,1);
        assertNotEquals(a,b);assertFalse(a.contains(segredo));assertEquals(segredo,c.abrir(a,1));
        assertThrows(ResponseStatusException.class,()->c.abrir(a,2));
        byte[] alterado=Base64.getDecoder().decode(a);alterado[alterado.length-1]^=1;
        assertThrows(ResponseStatusException.class,()->c.abrir(Base64.getEncoder().encodeToString(alterado),1));
    }
    @Test void chaveAusenteNaoCriaConfiguracaoDesprotegida() {
        var c=new MfaCifra("");assertFalse(c.disponivel());assertThrows(ResponseStatusException.class,()->c.cifrar("segredo",1));
        assertThrows(IllegalArgumentException.class,()->new MfaCifra("invalida"));
        assertThrows(IllegalArgumentException.class,()->new MfaCifra(Base64.getEncoder().encodeToString(new byte[16])));
    }
}
