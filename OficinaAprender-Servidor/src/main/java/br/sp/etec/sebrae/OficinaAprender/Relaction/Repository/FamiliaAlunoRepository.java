package br.sp.etec.sebrae.OficinaAprender.Relaction.Repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamiliaAlunoRepository extends JpaRepository<ProfissionalAluno, Long> {
    @Override
    @NonNull
    Optional<ProfissionalAluno> findById(@NonNull Long aLong);
}
