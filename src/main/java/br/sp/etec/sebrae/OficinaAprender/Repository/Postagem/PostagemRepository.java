package br.sp.etec.sebrae.OficinaAprender.Repository.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
    @Override
    @NonNull
    Optional<Postagem> findById(@NonNull Long aLong);
    List<Postagem> findByMetaIdOrderByDataCriacaoAscIdAsc(Long id);
    @org.springframework.data.jpa.repository.Query("select p.meta.id, count(p) from Postagem p where p.meta.paciente.id = :paciente and (:profissional = true or p.visualizacao = br.sp.etec.sebrae.OficinaAprender.Enuns.Visualizacao.TODOS) group by p.meta.id")
    List<Object[]> contarPorPaciente(@org.springframework.data.repository.query.Param("paciente") Long paciente, @org.springframework.data.repository.query.Param("profissional") boolean profissional);
    boolean existsByRespondeAId(Long id);
}
