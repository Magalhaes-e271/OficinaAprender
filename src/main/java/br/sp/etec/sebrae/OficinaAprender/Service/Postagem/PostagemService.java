package br.sp.etec.sebrae.OficinaAprender.Service.Postagem;

import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Meta.Meta;
import br.sp.etec.sebrae.OficinaAprender.Entity.Postagem.Postagem;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.Meta.MetaRepository;
import br.sp.etec.sebrae.OficinaAprender.Repository.Postagem.PostagemRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostagemService {

    private final PostagemRepository postagemRepository;

    private final MetaRepository metaRepository;

    private final TokenService tokenService;


    public PostagemService(

            PostagemRepository postagemRepository,

            MetaRepository metaRepository,

            TokenService tokenService

    ) {

        this.postagemRepository =
                postagemRepository;

        this.metaRepository =
                metaRepository;

        this.tokenService =
                tokenService;
    }


    public Optional<Postagem> findByID(

            Long id,

            String authorizationHeader

    ) {

        tokenService
                .validarAutorizacao(
                        authorizationHeader
                );


        return postagemRepository
                .findById(id);
    }


    public Postagem criar(

            Postagem postagem,

            Long idMeta,

            String authorizationHeader

    ) {

        tokenService
                .validarAutorizacao(
                        authorizationHeader
                );


        Meta meta =
                metaRepository
                        .findById(idMeta)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Meta não encontrada"
                                        )
                        );


        try {

            return postagemRepository
                    .save(postagem);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao salvar postagem",
                    e
            );
        }
    }
}