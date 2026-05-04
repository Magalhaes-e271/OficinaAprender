package br.sp.etec.sebrae.OficinaAprender.Repository.Usuario.Tipos;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Tipos.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {}
