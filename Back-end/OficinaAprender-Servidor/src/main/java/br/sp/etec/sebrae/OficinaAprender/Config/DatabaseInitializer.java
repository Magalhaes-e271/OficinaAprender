package br.sp.etec.sebrae.OficinaAprender.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void criarSchemas() {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS ASEGURANCA");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS APERFIL");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS ACONTEUDO");
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS ARELACAO");
    }
}
