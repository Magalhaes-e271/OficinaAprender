package br.sp.etec.sebrae.OficinaAprender;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.datasource.url=${TEST_DATABASE_URL:jdbc:postgresql://localhost:5436/oficina_aprender_test}",
    "spring.datasource.username=${TEST_DATABASE_USERNAME:oficina_user}",
    "spring.datasource.password=${TEST_DATABASE_PASSWORD:oficina_test}",
    "app.jwt.secret=segredo-exclusivo-dos-testes-com-mais-de-32-bytes",
    "app.cors.origins=http://localhost:5500"
})
class ApiIntegrationTests {
    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbc;
    @Autowired org.springframework.core.env.Environment ambiente;
    final JsonMapper json = JsonMapper.builder().build();
    final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    record Conta(long id, String email, String token) {}
    record ArquivoTeste(String nome, String tipo, byte[] bytes) {}
    record Resposta(int status, String texto, byte[] bytes) {}
    @BeforeEach void limparSomenteBancoDeTeste() {
        String banco = jdbc.queryForObject("select current_database()", String.class);
        assertTrue(banco != null && banco.endsWith("_test"), "Use um banco exclusivo com nome terminado em _test.");
        jdbc.execute("TRUNCATE ativacao_conta, compromisso, anexo, postagem, meta_dependencia, meta, relacao_usuario, perfil_usuario, usuario, arquivo RESTART IDENTITY CASCADE");
    }
    JsonNode dados(Resposta r) { return json.readTree(r.texto); }
    Resposta chamar(String metodo, String rota, String token, Object corpo) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + rota)).timeout(Duration.ofSeconds(40));
        if (token != null) request.header("Authorization", "Bearer " + token);
        if (corpo != null) request.header("Content-Type", "application/json");
        request.method(metodo, corpo == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(corpo)));
        var response = http.send(request.build(), HttpResponse.BodyHandlers.ofByteArray());
        return new Resposta(response.statusCode(), new String(response.body(), StandardCharsets.UTF_8), response.body());
    }
    Resposta multipart(String rota, String token, Object corpo, String campo, List<ArquivoTeste> arquivos) throws Exception {
        String limite = "oficina-" + UUID.randomUUID(); var body = new ByteArrayOutputStream();
        if (corpo != null) {
            body.write(("--" + limite + "\r\nContent-Disposition: form-data; name=\"dados\"\r\nContent-Type: application/json\r\n\r\n" + json.writeValueAsString(corpo) + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
        for (var f : arquivos) {
            body.write(("--" + limite + "\r\nContent-Disposition: form-data; name=\"" + campo + "\"; filename=\"" + f.nome + "\"\r\nContent-Type: " + f.tipo + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(f.bytes); body.write("\r\n".getBytes(StandardCharsets.UTF_8));
        }
        body.write(("--" + limite + "--\r\n").getBytes(StandardCharsets.UTF_8));
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + rota))
                .timeout(Duration.ofSeconds(40)).header("Content-Type", "multipart/form-data; boundary=" + limite);
        if (token != null) request.header("Authorization", "Bearer " + token);
        var response = http.send(request.POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray())).build(), HttpResponse.BodyHandlers.ofByteArray());
        return new Resposta(response.statusCode(), new String(response.body(), StandardCharsets.UTF_8), response.body());
    }
    byte[] png() throws Exception { var out = new ByteArrayOutputStream(); ImageIO.write(new BufferedImage(2,2,BufferedImage.TYPE_INT_RGB), "png", out); return out.toByteArray(); }
    Conta conta(String nome) throws Exception {
        String email = nome.toLowerCase(Locale.ROOT) + "@example.test";
        var r = multipart("/usuario/criar", null, Map.of("email",email,"senha","Senha123!","tipoUsuario","PROFISSIONAL",
                "perfilUsuario",Map.of("nome",nome,"cpf","52998224725","telefone","11900001234","dataNascimento","1995-06-15")),
                "foto", List.of(new ArquivoTeste("perfil.png","image/png",png())));
        assertEquals(201,r.status,r.texto);
        assertFalse(r.texto.contains("senha")); assertFalse(r.texto.contains("fotoArquivo"));
        var d = dados(r); return new Conta(d.path("usuario").path("id").asLong(),email,d.path("token").asText());
    }
    Resposta paciente(Conta autor, String email, Long... equipe) throws Exception {
        return chamar("POST","/paciente/criar",autor.token,Map.of("email",email,"perfilUsuario",
                Map.of("nome","Paciente Teste","dataNascimento","2015-01-02","condicoes","Condição de teste", "conteudoHtml","<p>Descrição do paciente</p>"),"profissionais",Arrays.asList(equipe)));
    }
    long meta(Conta autor, long paciente, String titulo, Long... anteriores) throws Exception {
        var r=chamar("POST","/meta/criar?idPaciente="+paciente,autor.token,Map.of("titulo",titulo,"area","Aprendizagem","conteudoHtml","<p>Meta</p>","anteriores",Arrays.asList(anteriores)));
        assertEquals(201,r.status,r.texto); assertEquals(autor.id,dados(r).path("responsavel").path("id").asLong());
        return dados(r).path("id").asLong();
    }
    Map<String,Object> post(String titulo, String visibilidade) {
        return Map.of("titulo",titulo,"tipoPostagem","EVOLUCAO","visualizacao",visibilidade,"conteudoHtml","<p>Texto <strong>seguro</strong></p><script>alert(1)</script><img src=x onerror=alert(1)>");
    }
    @Test void autenticacaoPerfilFotoEValidacao() throws Exception {
        var a=conta("Ana"); var b=conta("Beto");
        assertEquals(401,chamar("GET","/usuario/me",null,null).status);
        assertEquals(401,chamar("GET","/usuario/me",a.token+"x",null).status);
        assertEquals(401,chamar("POST","/login",null,Map.of("email",a.email,"senha","errada")).status);
        var login=chamar("POST","/login",null,Map.of("email","  ANA@EXAMPLE.TEST ","senha","Senha123!"));
        assertEquals(200,login.status,login.texto); assertEquals(a.id,dados(login).path("usuario").path("id").asLong());
        var legado=chamar("POST","/auth/login",null,Map.of("email",a.email,"senha","Senha123!"));
        assertEquals(200,legado.status,legado.texto); assertEquals(a.id,dados(legado).path("id").asLong()); assertTrue(dados(legado).hasNonNull("token"));
        assertEquals(403,chamar("GET","/usuario/id?id="+a.id,b.token,null).status);
        assertEquals(403,chamar("PUT","/usuario/editar?id="+a.id,b.token,Map.of("email","roubo@example.test")).status);
        assertEquals(403,chamar("DELETE","/usuario/deletar?id="+a.id,b.token,null).status);
        assertEquals(403,chamar("PUT","/usuario/descricao?id="+a.id,b.token,Map.of("nome","Intruso")).status);
        var r=chamar("PUT","/usuario/descricao?id="+a.id,a.token,Map.of("horarioDeEntrada","08:30","horarioDeSaida","17:00","condicoes","Campo preservado", "conteudoHtml","<p>Olá</p><script>malicioso()</script>"));
        assertEquals(200,r.status,r.texto); assertEquals("08:30:00",dados(r).path("horarioDeEntrada").asText());
        assertFalse(r.texto.contains("<script")); assertFalse(r.texto.contains("malicioso"));
        assertEquals("Campo preservado",dados(chamar("GET","/usuario/me",a.token,null)).path("perfil").path("condicoes").asText());
        assertArrayEquals(png(),chamar("GET","/usuario/foto?id="+a.id,a.token,null).bytes);
        assertEquals(403,multipart("/usuario/foto?id="+a.id,b.token,null,"arquivo",List.of(new ArquivoTeste("foto.png","image/png",png()))).status);
        assertEquals(200,multipart("/usuario/foto?id="+a.id,a.token,null,"arquivo",List.of(new ArquivoTeste("foto.png","image/png",png()))).status);
        int quantidade=jdbc.queryForObject("select count(*) from usuario",Integer.class);
        var invalido=multipart("/usuario/criar",null,Map.of("email","x@example.test","senha","Senha123!","tipoUsuario","PROFISSIONAL","perfilUsuario",Map.of("nome","Inválido")),"foto",List.of(new ArquivoTeste("falsa.png","image/png","não é imagem".getBytes(StandardCharsets.UTF_8))));
        assertEquals(400,invalido.status,invalido.texto); assertEquals(quantidade,jdbc.queryForObject("select count(*) from usuario",Integer.class));
        assertEquals(409,multipart("/usuario/criar",null,Map.of("email",a.email,"senha","Senha123!","tipoUsuario","PROFISSIONAL","perfilUsuario",Map.of("nome","Duplicado")),"foto",List.of(new ArquivoTeste("foto.png","image/png",png()))).status);
        assertEquals(400,chamar("PUT","/usuario/descricao?id="+a.id,a.token,Map.of("cpf","11111111111")).status);
        var mudanca=chamar("PUT","/usuario/senha",a.token,Map.of("senhaAtual","Senha123!","novaSenha","NovaSenha123!"));
        assertEquals(204,mudanca.status,mudanca.texto); assertEquals(401,chamar("GET","/usuario/me",a.token,null).status);
    }
    @Test void pacientesEquipeConvitesIdempotenciaERevogacao() throws Exception {
        var a=conta("Ana");var b=conta("Beto");var c=conta("Caio");
        var r=paciente(a,"paciente@example.test",b.id);assertEquals(201,r.status,r.texto);long id=dados(r).path("id").asLong();
        assertEquals(id,dados(paciente(a,"paciente@example.test",b.id)).path("id").asLong());
        assertEquals(1,dados(chamar("GET","/paciente",a.token,null)).size());
        assertEquals(0,dados(chamar("GET","/paciente",b.token,null)).size());
        assertEquals(403,chamar("GET","/paciente/"+id,b.token,null).status);
        assertEquals(403,chamar("GET","/meta?idPaciente="+id,b.token,null).status);
        var lista=chamar("GET","/usuario-aluno",b.token,null);assertEquals(200,lista.status,lista.texto);
        assertFalse(lista.texto.contains("cpf"));assertFalse(lista.texto.contains("condicoes"));assertFalse(lista.texto.contains("senha"));
        long convite=dados(lista).get(0).path("id").asLong();
        assertEquals(403,chamar("PUT","/usuario-aluno/"+convite+"/aceitar",c.token,Map.of()).status);
        assertEquals(200,chamar("PUT","/usuario-aluno/"+convite+"/aceitar",b.token,Map.of()).status);
        assertEquals(1,dados(chamar("GET","/paciente",b.token,null)).size());
        assertEquals(200,chamar("GET","/paciente/"+id,b.token,null).status);
        assertEquals(409,chamar("POST","/paciente/"+id+"/equipe",a.token,Map.of("idProfissional",b.id)).status);
        assertEquals(404,paciente(a,"nao-gravar@example.test",99999L).status);
        assertEquals(0,jdbc.queryForObject("select count(*) from usuario where email='nao-gravar@example.test'",Integer.class));
        var busca=chamar("GET","/usuario/buscar?nome=Beto",a.token,null);assertEquals(200,busca.status);assertFalse(busca.texto.contains("cpf"));
        long m=meta(b,id,"Meta da equipe");
        assertEquals(204,chamar("DELETE","/usuario-aluno/"+convite,a.token,null).status);
        assertEquals(403,chamar("GET","/meta/"+m,b.token,null).status);
        assertEquals(200,chamar("GET","/meta/"+m,a.token,null).status);
        assertEquals(400,chamar("POST","/usuario-aluno/criar",c.token,Map.of("usuarioDestinatario",Map.of("id",c.id))).status);
    }
    @Test void metasDependenciasConclusaoECiclos() throws Exception {
        var a=conta("Ana");long p=dados(paciente(a,"p@example.test")).path("id").asLong();
        long outra=dados(paciente(a,"outra@example.test")).path("id").asLong();
        long m1=meta(a,p,"Primeira");long m2=meta(a,p,"Segunda",m1);
        assertEquals(400,chamar("PATCH","/meta/"+m1,a.token,Map.of("anteriores",List.of(m2))).status);
        assertEquals(400,chamar("POST","/meta/criar?idPaciente="+outra,a.token,Map.of("titulo","Incompatível","area","Área","anteriores",List.of(m1))).status);
        assertEquals(409,chamar("PATCH","/meta/"+m2,a.token,Map.of("concluido",true)).status);
        assertEquals(200,chamar("PATCH","/meta/"+m1,a.token,Map.of("concluido",true)).status);
        assertEquals(200,chamar("PATCH","/meta/"+m2,a.token,Map.of("concluido",true)).status);
        assertEquals(409,chamar("PATCH","/meta/"+m1,a.token,Map.of("concluido",false)).status);
        assertEquals(200,chamar("PATCH","/meta/"+m2,a.token,Map.of("concluido",false)).status);
        assertEquals(200,chamar("PATCH","/meta/"+m1,a.token,Map.of("concluido",false)).status);
        assertEquals(409,chamar("DELETE","/meta/"+m1,a.token,null).status);
    }
    @Test void postagensArquivosVisibilidadeRespostasEAtivacao() throws Exception {
        var a=conta("Ana");var c=conta("Caio");long p=dados(paciente(a,"p@example.test")).path("id").asLong();long m=meta(a,p,"Meta");
        byte[] pdf="%PDF-1.4\nArquivo de teste".getBytes(StandardCharsets.UTF_8);
        var r=multipart("/postagem/publicar?idMeta="+m,a.token,post("Registro","PROFISSIONAIS"),"arquivos",List.of(new ArquivoTeste("registro.pdf","application/pdf",pdf)));
        assertEquals(201,r.status,r.texto);assertFalse(r.texto.contains("onerror"));assertFalse(r.texto.contains("<script"));assertFalse(r.texto.contains("senha"));
        long postagem=dados(r).path("id").asLong();long anexo=dados(r).path("anexos").get(0).path("id").asLong();
        assertEquals(m,dados(r).path("idMeta").asLong());assertEquals(a.id,dados(r).path("autor").path("id").asLong());
        assertArrayEquals(pdf,chamar("GET","/anexo/"+anexo+"/arquivo",a.token,null).bytes);
        assertEquals(403,chamar("GET","/anexo/"+anexo+"/arquivo",c.token,null).status);
        assertEquals(403,chamar("GET","/postagem?idMeta="+m,c.token,null).status);
        int total=jdbc.queryForObject("select count(*) from postagem",Integer.class);int blobs=jdbc.queryForObject("select count(*) from arquivo",Integer.class);
        var falha=multipart("/postagem/publicar?idMeta="+m,a.token,post("Falha","TODOS"),"arquivos",List.of(new ArquivoTeste("ok.pdf","application/pdf",pdf),new ArquivoTeste("ataque.svg","image/svg+xml","<svg/>".getBytes(StandardCharsets.UTF_8))));
        assertEquals(400,falha.status,falha.texto);assertEquals(total,jdbc.queryForObject("select count(*) from postagem",Integer.class));assertEquals(blobs,jdbc.queryForObject("select count(*) from arquivo",Integer.class));
        var ativacao=chamar("POST","/paciente/"+p+"/ativacao",a.token,Map.of());assertEquals(200,ativacao.status,ativacao.texto);String token=dados(ativacao).path("token").asText();
        var entrada=chamar("POST","/auth/ativar",null,Map.of("token",token,"senha","SenhaPaciente123!"));assertEquals(200,entrada.status,entrada.texto);String pacienteToken=dados(entrada).path("token").asText();
        assertEquals(400,chamar("POST","/auth/ativar",null,Map.of("token",token,"senha","SenhaPaciente123!")).status);
        assertEquals(0,dados(chamar("GET","/postagem?idMeta="+m,pacienteToken,null)).size());
        assertEquals(403,chamar("GET","/anexo/"+anexo+"/arquivo",pacienteToken,null).status);
        assertEquals(403,chamar("POST","/meta/criar?idPaciente="+p,pacienteToken,Map.of("titulo","Não autorizado","area","Área")).status);
        assertEquals(403,chamar("POST","/postagem/criar?idMeta="+m,pacienteToken,post("Restrito","PROFISSIONAIS")).status);
        var publico=chamar("POST","/postagem/criar?idMeta="+m,a.token,post("Público","TODOS"));assertEquals(201,publico.status,publico.texto);
        long publicoId=dados(publico).path("id").asLong();
        var resposta=new HashMap<String,Object>(post("Resposta","TODOS"));resposta.put("respondeA",publicoId);
        var criado=chamar("POST","/postagem/criar?idMeta="+m,pacienteToken,resposta);assertEquals(201,criado.status,criado.texto);assertEquals(p,dados(criado).path("autor").path("id").asLong());
        assertEquals(409,chamar("DELETE","/postagem/"+publicoId,a.token,null).status);
        assertEquals(204,chamar("DELETE","/anexo/"+anexo,a.token,null).status);
        assertEquals(404,chamar("GET","/anexo/"+anexo+"/arquivo",a.token,null).status);
        assertEquals(blobs-1,jdbc.queryForObject("select count(*) from arquivo",Integer.class));
    }
    @Test void agendaConflitosLimitesConcorrenciaEAutorizacao() throws Exception {
        var a=conta("Ana");var b=conta("Beto");long p=dados(paciente(a,"p@example.test",b.id)).path("id").asLong();
        long convite=dados(chamar("GET","/usuario-aluno",b.token,null)).get(0).path("id").asLong();
        assertEquals(200,chamar("PUT","/usuario-aluno/"+convite+"/aceitar",b.token,Map.of()).status);
        var entrada=Map.of("titulo","Atendimento","idPaciente",p,"inicio","2026-10-20T12:00:00Z","fim","2026-10-20T13:00:00Z");
        var criado=chamar("POST","/agenda/criar",a.token,entrada);assertEquals(201,criado.status,criado.texto);long id=dados(criado).path("id").asLong();
        assertEquals(409,chamar("POST","/agenda/criar",b.token,entrada).status);
        assertEquals(403,chamar("DELETE","/agenda/"+id,b.token,null).status);
        assertEquals(0,dados(chamar("GET","/agenda",b.token,null)).size());
        assertEquals(400,chamar("POST","/agenda/criar",a.token,Map.of("titulo","Inválido","inicio","2026-10-20T13:00:00Z","fim","2026-10-20T12:00:00Z")).status);
        assertEquals(201,chamar("POST","/agenda/criar",a.token,Map.of("titulo","Limite","inicio","2026-10-20T13:00:00Z","fim","2026-10-20T14:00:00Z")).status);
        var simultaneo=Map.of("titulo","Concorrente","idPaciente",p,"inicio","2026-10-21T12:00:00Z","fim","2026-10-21T13:00:00Z");
        try (var executor=Executors.newFixedThreadPool(6)) {
            var largada=new CountDownLatch(1);List<Future<Resposta>> futuros=new ArrayList<>();
            for(int i=0;i<6;i++) futuros.add(executor.submit(()->{largada.await();return chamar("POST","/agenda/criar",a.token,simultaneo);}));
            largada.countDown();List<Integer> status=new ArrayList<>();for(var f:futuros)status.add(f.get(50,TimeUnit.SECONDS).status);
            assertEquals(1,status.stream().filter(s->s==201).count(),status.toString());assertEquals(5,status.stream().filter(s->s==409).count(),status.toString());
        }
        assertEquals(204,chamar("DELETE","/agenda/"+id,a.token,null).status);
        assertEquals(201,chamar("POST","/agenda/criar",a.token,entrada).status);
    }
    @Test void migracaoPreservaDadosDeUmBancoLegado() throws Exception {
        String schema = "legado_migracao_test";
        jdbc.execute("CREATE SCHEMA " + schema);
        try {
            var ds = new org.springframework.jdbc.datasource.DriverManagerDataSource(
                    ambiente.getRequiredProperty("spring.datasource.url"),
                    ambiente.getRequiredProperty("spring.datasource.username"),
                    ambiente.getRequiredProperty("spring.datasource.password"));
            try (var conn = ds.getConnection(); var statement = conn.createStatement()) {
                conn.setSchema(schema);
                String sql = new String(Objects.requireNonNull(getClass().getResourceAsStream("/db/migration/V1__estrutura_original.sql")).readAllBytes(), StandardCharsets.UTF_8);
                statement.execute(sql);
                statement.execute("INSERT INTO usuario(id,email,senha,tipo_usuario) VALUES (1,'profissional@example.test','hash-teste','PROFISSIONAL'),(2,'paciente@example.test','hash-teste','PACIENTE')");
                statement.execute("INSERT INTO relacao_usuario(id,id_usuario_solicitante,id_usuario_destinatario,tipo_relacao,solicitacao) VALUES (1,1,2,'PROFISSIONAL_PACIENTE',1)");
                statement.execute("INSERT INTO meta(id,id_relacao_usuario,titulo,concluido) VALUES (1,1,'Registro preservado',false)");
            }
            org.flywaydb.core.Flyway.configure().dataSource(ds).schemas(schema).defaultSchema(schema)
                    .baselineOnMigrate(true).baselineVersion("0").load().migrate();
            assertEquals("Registro preservado",jdbc.queryForObject("select titulo from " + schema + ".meta where id=1",String.class));
            assertEquals(2L,jdbc.queryForObject("select paciente_id from " + schema + ".meta where id=1",Long.class));
            assertEquals(1L,jdbc.queryForObject("select responsavel_id from " + schema + ".meta where id=1",Long.class));
            assertEquals(2,jdbc.queryForObject("select count(*) from " + schema + ".usuario",Integer.class));
        } finally { jdbc.execute("DROP SCHEMA " + schema + " CASCADE"); }
    }

}
