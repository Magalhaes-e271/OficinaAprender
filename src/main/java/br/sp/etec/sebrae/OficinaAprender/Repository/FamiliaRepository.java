package br.sp.etec.sebrae.OficinaAprender.Repository;

import br.sp.etec.sebrae.OficinaAprender.entities.PessoaFamilia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamiliaRepository extends JpaRepository<PessoaFamilia, Long> {}