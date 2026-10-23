package br.sp.etec.sebrae.OficinaAprender.Repository.Agenda;
import br.sp.etec.sebrae.OficinaAprender.Entity.Agenda.Compromisso;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

public interface CompromissoRepository extends JpaRepository<Compromisso, Long> {
    List<Compromisso> findByProfissionalIdOrderByInicioAsc(Long id);
    @Query("select (count(c) > 0) from Compromisso c where (c.profissional.id = :profissional or (:paciente is not null and c.paciente.id = :paciente)) and c.inicio < :fim and c.fim > :inicio")
    boolean conflito(@Param("profissional") Long profissional, @Param("paciente") Long paciente,
                     @Param("inicio") Instant inicio, @Param("fim") Instant fim);
}
