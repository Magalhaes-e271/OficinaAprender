package br.sp.etec.sebrae.OficinaAprender.Controller.Publicacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Publicacao.Publicacao;
import br.sp.etec.sebrae.OficinaAprender.Service.Publicacao.Anexo.AnexoService;
import br.sp.etec.sebrae.OficinaAprender.Service.Publicacao.PublicacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/publicacao")
public class PublicacaoController {

    @PostMapping("/salvar")
    public ResponseEntity<?> salvar(@RequestBody Publicacao publicacao){
    publicacao.getAnexos().forEach(anexo -> anexo.setPublicacao(publicacao));
    publicacao.getAnexos().forEach(AnexoService::salvar);
        return ResponseEntity.ok().body( PublicacaoService.salvar(publicacao));

    }

}
