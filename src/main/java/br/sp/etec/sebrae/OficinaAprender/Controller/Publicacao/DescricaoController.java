package br.sp.etec.sebrae.OficinaAprender.Controller.Publicacao;


import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Descricao;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.DescricaoService;
import br.sp.etec.sebrae.OficinaAprender.res.TipoDescricao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/descricao")
public class DescricaoController {
    private final DescricaoService descricaoService;

    public DescricaoController(DescricaoService descricaoService) {
        this.descricaoService = descricaoService;
    }

    @PostMapping("/criar")
    public ResponseEntity<?> criar(@RequestBody Descricao descricao){
        Descricao descricaoSalva = DescricaoService.salvar(descricao);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(descricaoSalva);
    }

    @PostMapping("{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(descricaoService.findByID(id));
    }

    @PostMapping("/editar")
    public ResponseEntity<?> editar(@RequestBody Descricao descricao){
        if(buscarPorId(descricao.getId()).getBody() == null){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Não foi possivel achar essa descricao");
        }else {

        Descricao descricaoSalva = DescricaoService.salvar(descricao);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(descricaoSalva);
        }
    }

    @PostMapping("/verificarTipo")
    public ResponseEntity<?> verificarTipo(@RequestBody Descricao descricao){
        if(descricao.getTipoDescricao() == TipoDescricao.ALUNO){
            return ResponseEntity
                    .status(HttpStatus.MULTIPLE_CHOICES)
                    .body(TipoDescricao.ALUNO);
        } else {
            return  ResponseEntity
                    .status(HttpStatus.MULTIPLE_CHOICES)
                    .body(TipoDescricao.USUARIO);
        }
    }
}
