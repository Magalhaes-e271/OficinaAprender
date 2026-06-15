package br.sp.etec.sebrae.OficinaAprender.Controller.Publicacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Anexo.AnexoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Postagem.PostagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/publicacao")
public class PublicacaoController {

    @PutMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody Postagem postagem){
    postagem.getAnexos().forEach(anexo -> anexo.setPostagem(postagem));
    postagem.getAnexos().forEach(AnexoService::salvar);
        return ResponseEntity.ok().body( PostagemService.salvar(postagem));

    }

}
