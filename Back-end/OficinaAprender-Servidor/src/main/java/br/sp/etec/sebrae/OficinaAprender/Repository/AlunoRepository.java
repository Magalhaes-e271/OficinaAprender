package br.sp.etec.sebrae.OficinaAprender.Repository;

import br.sp.etec.sebrae.OficinaAprender.entities.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {}

