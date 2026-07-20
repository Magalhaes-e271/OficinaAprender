package br.sp.etec.sebrae.OficinaAprender.Repository;

import br.sp.etec.sebrae.OficinaAprender.Entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

}

