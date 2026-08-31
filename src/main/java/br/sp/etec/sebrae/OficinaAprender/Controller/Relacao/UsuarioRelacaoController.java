package br.sp.etec.sebrae.OficinaAprender.Controller.Relacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import br.sp.etec.sebrae.OficinaAprender.Service.Relacao.RelacaoUsuarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario-aluno")
public class UsuarioRelacaoController {
   public RelacaoUsuarioService relacaoUsuarioService;
   UsuarioRelacaoController(RelacaoUsuarioService relacaoUsuarioService){
       this.relacaoUsuarioService = relacaoUsuarioService;
   }
    @PostMapping("/criar")
    public ResponseEntity<?> criar(RelacaoUsuario relacaoUsuario){
    return relacaoUsuarioService.criar(relacaoUsuario);
    }

    @PutMapping(value = "/editar")
    public ResponseEntity<?> editar(RelacaoUsuario relacaoUsuario){
       relacaoUsuarioService.salvar(relacaoUsuario);
        return ResponseEntity.ok().build();
    }
}
