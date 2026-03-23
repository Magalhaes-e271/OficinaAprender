package br.sp.etec.sebrae.OficinaAprender.Relações.Repository;

import br.sp.etec.sebrae.OficinaAprender.Relações.ProfissionalAluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamiliaAlunoRepository extends JpaRepository<ProfissionalAluno, Long> {}
