package br.sp.etec.sebrae.OficinaAprender.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class HealthController {
    private final JdbcTemplate jdbc;
    @GetMapping("/health") public ResponseEntity<?> health() {
        try {
            jdbc.queryForObject("select count(*) from flyway_schema_history where success = true", Integer.class);
            jdbc.queryForObject("select count(*) from app_segredo", Integer.class);
            return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(Map.of("status", "UP", "database", "UP", "storage", "POSTGRESQL", "version", "1.0.0"));
        } catch (Exception e) { return ResponseEntity.status(503).cacheControl(CacheControl.noStore()).body(Map.of("status", "DOWN")); }
    }
}
