package br.sp.etec.sebrae.OficinaAprender.Relações.Repository;

import br.sp.etec.sebrae.OficinaAprender.Relações.ProfissionalAluno;
import br.sp.etec.sebrae.OficinaAprender.entities.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalAlunoRepository extends JpaRepository<ProfissionalAluno, Long>{}
