package br.sp.etec.sebrae.OficinaAprender.Repository.Relation;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.PostagemRelacionada;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostagemRelacionadaRepository extends JpaRepository<PostagemRelacionada, Long> {
    @Override
    Optional<PostagemRelacionada> findById(Long aLong);
}
