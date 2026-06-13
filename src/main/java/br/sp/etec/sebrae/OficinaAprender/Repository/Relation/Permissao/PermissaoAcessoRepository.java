package br.sp.etec.sebrae.OficinaAprender.Repository.Relation.Permissao;


import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.Permissao.PermissaoAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissaoAcessoRepository extends JpaRepository<PermissaoAcesso, Long> {
    @Override
    Optional<PermissaoAcesso> findById(Long aLong);
}
