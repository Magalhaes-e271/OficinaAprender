package br.sp.etec.sebrae.OficinaAprender.Relações.Repository;

import br.sp.etec.sebrae.OficinaAprender.Relações.Profissional_Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamiliaAlunoRepository extends JpaRepository<Profissional_Aluno, Long> {}
