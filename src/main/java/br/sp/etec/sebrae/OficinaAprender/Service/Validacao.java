package br.sp.etec.sebrae.OficinaAprender.Service;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.PerfilUsuario.PerfilUsuario;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Locale;

public final class Validacao {
    private Validacao() {}
    public static ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
    public static String texto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) throw erro(HttpStatus.BAD_REQUEST, "Informe " + campo + ".");
        if (valor.trim().length() > maximo) throw erro(HttpStatus.BAD_REQUEST, campo + " excede " + maximo + " caracteres.");
        return valor.trim();
    }
    public static String opcional(String valor, String campo, int maximo) {
        if (valor == null) return null;
        if (valor.length() > maximo) throw erro(HttpStatus.BAD_REQUEST, campo + " excede " + maximo + " caracteres.");
        return valor.trim();
    }
    public static String email(String email) {
        String normalizado = texto(email, "o e-mail", 254).toLowerCase(Locale.ROOT);
        if (!normalizado.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw erro(HttpStatus.BAD_REQUEST, "Informe um e-mail válido.");
        return normalizado;
    }
    public static void senha(String senha) {
        if (senha == null || senha.length() < 8 || senha.getBytes(StandardCharsets.UTF_8).length > 72)
            throw erro(HttpStatus.BAD_REQUEST, "A senha deve ter ao menos 8 caracteres e no máximo 72 bytes.");
    }
    public static String html(String valor) {
        if (valor == null) return "";
        if (valor.length() > 100_000) throw erro(HttpStatus.BAD_REQUEST, "O texto deve ter até 100 mil caracteres.");
        return Jsoup.clean(valor, Safelist.basic().addTags("h1", "h2", "h3", "h4", "div", "span", "hr", "u")
                .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer"));
    }
    public static void perfil(PerfilUsuario p, boolean profissional) {
        if (p == null) throw erro(HttpStatus.BAD_REQUEST, "Informe os dados do perfil.");
        p.setNome(texto(p.getNome(), "o nome", 200));
        p.setTelefone(opcional(p.getTelefone(), "O telefone", 30));
        if (p.getTelefone() != null && !p.getTelefone().isBlank()) {
            String numero = p.getTelefone().replaceAll("\\D", "");
            if (numero.length() < 10 || numero.length() > 13) throw erro(HttpStatus.BAD_REQUEST, "Informe um telefone válido.");
            p.setTelefone(numero);
        }
        if (p.getCpf() != null && !p.getCpf().isBlank()) {
            String cpf = p.getCpf().replaceAll("\\D", "");
            if (!cpfValido(cpf)) throw erro(HttpStatus.BAD_REQUEST, "Informe um CPF válido.");
            p.setCpf(cpf);
        }
        if (p.getDataNascimento() != null) {
            LocalDate nascimento = p.getDataNascimento().toLocalDate();
            if (nascimento.isAfter(LocalDate.now()) || nascimento.isBefore(LocalDate.now().minusYears(130)))
                throw erro(HttpStatus.BAD_REQUEST, "Informe uma data de nascimento válida.");
            if (profissional && nascimento.isAfter(LocalDate.now().minusYears(16)))
                throw erro(HttpStatus.BAD_REQUEST, "O profissional deve ter pelo menos 16 anos.");
        }
        if (p.getHorarioDeEntrada() != null && p.getHorarioDeSaida() != null &&
                !p.getHorarioDeSaida().isAfter(p.getHorarioDeEntrada()))
            throw erro(HttpStatus.BAD_REQUEST, "O horário de saída deve ser depois da entrada.");
        p.setOcupacao(opcional(p.getOcupacao(), "A ocupação", 200));
        p.setInstituicao(opcional(p.getInstituicao(), "A instituição", 200));
        p.setCondicao(opcional(p.getCondicao(), "A condição", 2000));
        p.setConteudoHtml(html(p.getConteudoHtml()));
    }
    static boolean cpfValido(String cpf) {
        if (!cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) return false;
        for (int posicao = 9; posicao <= 10; posicao++) {
            int soma = 0;
            for (int i = 0; i < posicao; i++) soma += (cpf.charAt(i) - '0') * (posicao + 1 - i);
            int digito = (soma * 10) % 11;
            if (digito == 10) digito = 0;
            if (digito != cpf.charAt(posicao) - '0') return false;
        }
        return true;
    }
}
