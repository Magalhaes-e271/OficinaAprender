package br.sp.etec.sebrae.OficinaAprender.Repository.Relation;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.UsuarioPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioAlunoRepository extends JpaRepository<UsuarioPaciente, Long>{
    @Override
    Optional<UsuarioPaciente> findById(Long aLong);
}
