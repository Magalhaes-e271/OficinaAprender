package br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Meta;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {
    @Override
    @NonNull
    Optional<Meta> findById(@NonNull Long aLong);
}
