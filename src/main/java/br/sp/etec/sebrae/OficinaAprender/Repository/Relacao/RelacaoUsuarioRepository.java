package br.sp.etec.sebrae.OficinaAprender.Repository.Relacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelacaoUsuarioRepository extends JpaRepository<RelacaoUsuario, Long>{
    @Override
    Optional<RelacaoUsuario> findById(Long aLong);
}
