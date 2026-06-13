package br.sp.etec.sebrae.OficinaAprender.Repository.Usuario;

import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Override
    Optional<Usuario> findById(Long aLong);
}
