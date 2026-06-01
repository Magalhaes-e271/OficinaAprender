package br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DescricaoRepository extends JpaRepository<Descricao, Long> {
    @Override
    @NonNull
    Optional<Descricao> findById(@NonNull Long aLong);
}
