package br.sp.etec.sebrae.OficinaAprender.Repository;

import br.sp.etec.sebrae.OficinaAprender.entities.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacaoRepository extends JpaRepository<Profissional, Long> {}
