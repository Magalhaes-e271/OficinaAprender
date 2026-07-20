package br.sp.etec.sebrae.OficinaAprender.Controller.Relation.PermissaoAcesso;

import br.sp.etec.sebrae.OficinaAprender.Entity.Paciente;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.Permissao.PermissaoAcesso;
import br.sp.etec.sebrae.OficinaAprender.Entity.Relation.UsuarioPaciente;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.DescricaoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Relation.Permissao.PermissaoAcessoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Usuario.UsuarioService;
import br.sp.etec.sebrae.OficinaAprender.res.Permissao;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/permissao-acesso")
public class PermissaoAcessoController {

    PermissaoAcessoService pAService;
    public PermissaoAcessoController(  PermissaoAcessoService pAService) {
        this.pAService = pAService;
    }

    @PostMapping("/Criar")
    public ResponseEntity<?> criar(@RequestBody UsuarioPaciente UsrPac, @RequestBody Postagem post, @RequestAttribute Permissao permicao){
        return  new ResponseEntity(PermissaoAcessoService.criar(UsrPac,post,permicao).getStatusCode());

    }
}
