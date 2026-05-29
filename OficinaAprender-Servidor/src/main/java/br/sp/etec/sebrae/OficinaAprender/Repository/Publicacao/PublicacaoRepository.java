package br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PublicacaoRepository extends JpaRepository<Postagem, Long> {
    @Override
    @NonNull
    Optional<Postagem> findById(@NonNull Long aLong);
}
