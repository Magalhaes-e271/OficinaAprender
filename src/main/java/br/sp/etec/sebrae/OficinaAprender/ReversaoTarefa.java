package br.sp.etec.sebrae.OficinaAprender;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Manutenção temporária para atender ao pedido de desfazer a tarefa. */
@Component
public class ReversaoTarefa implements CommandLineRunner {
    private final JdbcTemplate jdbc;
    public ReversaoTarefa(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        jdbc.execute(new ClassPathResource("rollback-tarefa.sql").getContentAsString(StandardCharsets.UTF_8));
        System.out.println("REVERSAO_TAREFA_CONCLUIDA");
    }
}
