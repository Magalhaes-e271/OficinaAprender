package br.sp.etec.sebrae.OficinaAprender.Service.Postagem.Meta;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Meta.MetaRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MetaService {

    private final MetaRepository metaRepository;

    private final TokenService tokenService;


    public MetaService(

            MetaRepository metaRepository,

            TokenService tokenService

    ) {

        this.metaRepository =
                metaRepository;

        this.tokenService =
                tokenService;
    }


    public ResponseEntity<?> salvar(

            Meta meta,

            String authorizationHeader

    ) {

        tokenService
                .validarAutorizacao(
                        authorizationHeader
                );


        return ResponseEntity.ok(

                metaRepository
                        .save(meta)
        );
    }
}