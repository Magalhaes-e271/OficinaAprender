package br.sp.etec.sebrae.OficinaAprender.Relaction.Repository;

import br.sp.etec.sebrae.OficinaAprender.Relaction.UsuarioAluno;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.Optional;
import java.util.function.Function;

public interface UsuarioAlunoRepository extends JpaRepository<UsuarioAluno, Long>{
    @Override
    Optional<UsuarioAluno> findById(Long aLong);
}
