package br.sp.etec.sebrae.OficinaAprender.Service.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;
import br.sp.etec.sebrae.OficinaAprender.Repository.PacienteRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Publicacao.DescricaoRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.swing.undo.UndoableEditSupport;
import java.util.Optional;

@Service
public class DescricaoService {

    private static DescricaoRepository descricaoRepository;
    private static UsuarioService usuarioService;
    public DescricaoService(DescricaoRepository descricaoRepository, UsuarioService usuarioService) {
        this.descricaoRepository = descricaoRepository;
        this.usuarioService = usuarioService;
    }

    public static void setDescricaoRepository(DescricaoRepository descricaoRepository) {
        DescricaoService.descricaoRepository = descricaoRepository;
    }
    public Optional<Descricao> findByID(Long id) { return descricaoRepository.findById(id); }

    public static ResponseEntity<?> findByEmail(String email) {
        Descricao des = descricaoRepository.findByEmail(email).orElse(null);
        if(des == null){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não foi possivel encontrar uma descricao com esse email");
        } return ResponseEntity.ok(des);
    }
    public static ResponseEntity<?> salvar(Descricao descricao) {
      if(usuarioService.findByEmail(descricao.getEmail()) == null){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("não foi possivel encontrar esse usuario");
      }
        descricaoRepository.save(descricao);
      return ResponseEntity.ok("descricao salva com sucesso"); }

    public static ResponseEntity<?> criar(Descricao descricao) {
        return ResponseEntity.ok(descricaoRepository.save(descricao));
    }

}
