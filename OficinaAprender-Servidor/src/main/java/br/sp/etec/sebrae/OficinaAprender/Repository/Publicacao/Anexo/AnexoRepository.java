package br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao.Anexo;

import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Anexo.Anexo;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnexoRepository extends JpaRepository<Anexo, Long> {
    @Override
    @NonNull
    Optional<Anexo> findById(@NonNull Long aLong);
}
