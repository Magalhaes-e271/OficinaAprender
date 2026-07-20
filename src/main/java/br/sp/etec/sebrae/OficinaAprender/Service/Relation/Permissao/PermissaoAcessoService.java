package br.sp.etec.sebrae.OficinaAprender.Service.Relation.Permissao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.Permissao.PermissaoAcesso;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.UsuarioPaciente;
import br.sp.etec.sebrae.OficinaAprender.Repository.Relation.Permissao.PermissaoAcessoRepository;
import br.sp.etec.sebrae.OficinaAprender.res.Permissao;
import com.zaxxer.hikari.metrics.PoolStats;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PermissaoAcessoService {

    private static PermissaoAcessoRepository permissaoAcessoRepository;
    private static PermissaoAcesso permicao;

    public static Optional<PermissaoAcesso> findByID(Long id){return permissaoAcessoRepository.findById(id);}

    public static ResponseEntity<?> criar(UsuarioPaciente usrpac, Postagem post, Permissao permicao){
        if((usrpac == null)||(post == null)) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("usuario ou postagem nao encontrado");
        }
        PermissaoAcesso pa = new PermissaoAcesso();
        pa.setPermissao(permicao);
        pa.setUsuarioPaciente(usrpac);
        pa.setPostagem(post);
        return ResponseEntity.ok(permissaoAcessoRepository.save(pa));
    }
    public static ResponseEntity<?> salvar(PermissaoAcesso pa){
        if(findByID(pa.getId()).isPresent()){
            return ResponseEntity.ok(permissaoAcessoRepository.save(pa));
        } return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possivel encontrar essa conexão");
        }

}
