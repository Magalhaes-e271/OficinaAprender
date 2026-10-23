package br.sp.etec.sebrae.OficinaAprender.Repository.Arquivo;
import br.sp.etec.sebrae.OficinaAprender.Entity.Arquivo.Arquivo;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {}
