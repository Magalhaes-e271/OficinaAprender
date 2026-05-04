package br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao;
import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Publicacao;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PublicacaoRepository extends JpaRepository<Publicacao, Long> {
    @Override
    @NonNull
    Optional<Publicacao> findById(@NonNull Long aLong);
}
