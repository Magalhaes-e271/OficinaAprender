package br.sp.etec.sebrae.OficinaAprender.Repository.Relacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import br.sp.etec.sebrae.OficinaAprender.Enuns.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RelacaoUsuarioRepository extends JpaRepository<RelacaoUsuario, Long>{
    @Override
    Optional<RelacaoUsuario> findById(Long aLong);

    @Query("select r from RelacaoUsuario r where r.usuarioSolicitante.id = :id or r.usuarioDestinatario.id = :id order by r.dataSolicitacao desc")
    List<RelacaoUsuario> participantes(@Param("id") Long id);

    @Query("select (count(r) > 0) from RelacaoUsuario r where r.tipoRelacao = :tipo and r.solicitacao = :estado and ((r.usuarioSolicitante.id = :a and r.usuarioDestinatario.id = :b) or (r.usuarioSolicitante.id = :b and r.usuarioDestinatario.id = :a))")
    boolean vinculoAceito(@Param("a") Long a, @Param("b") Long b, @Param("tipo") TipoRelacao tipo, @Param("estado") Solicitacao estado);

    @Query("select r from RelacaoUsuario r where (r.usuarioSolicitante.id = :a and r.usuarioDestinatario.id = :b) or (r.usuarioSolicitante.id = :b and r.usuarioDestinatario.id = :a)")
    List<RelacaoUsuario> entre(@Param("a") Long a, @Param("b") Long b);
}
