package br.sp.etec.sebrae.OficinaAprender.Repository.Usuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
     Optional<Usuario> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> bloquear(@Param("id") Long id);

    @Query("select u from Usuario u join PerfilUsuario p on p.id = u.id where u.ativo = true and u.tipoUsuario = br.sp.etec.sebrae.OficinaAprender.Enuns.TipoUsuario.PROFISSIONAL and lower(p.nome) like lower(:nome) order by p.nome")
    List<Usuario> buscarProfissionais(@Param("nome") String nome, org.springframework.data.domain.Pageable pagina);

    @Override
    Optional<Usuario> findById(Long aLong);
}
