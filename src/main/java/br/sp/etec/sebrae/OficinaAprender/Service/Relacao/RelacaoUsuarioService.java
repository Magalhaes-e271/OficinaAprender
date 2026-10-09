package br.sp.etec.sebrae.OficinaAprender.Service.Relacao;

import br.sp.etec.sebrae.OficinaAprender.Entity.Relacao.RelacaoUsuario;
import br.sp.etec.sebrae.OficinaAprender.Repository.Relacao.RelacaoUsuarioRepository;
import br.sp.etec.sebrae.OficinaAprender.Service.Token.TokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelacaoUsuarioService {

    private final RelacaoUsuarioRepository relacaoUsuarioRepository;

    private final TokenService tokenService;


    public RelacaoUsuarioService(

            RelacaoUsuarioRepository relacaoUsuarioRepository,

            TokenService tokenService

    ) {

        this.relacaoUsuarioRepository =
                relacaoUsuarioRepository;

        this.tokenService =
                tokenService;
    }


    public RelacaoUsuario salvar(

            RelacaoUsuario relacaoUsuario,

            String authorizationHeader

    ) {

        tokenService
                .validarAutorizacao(
                        authorizationHeader
                );


        return relacaoUsuarioRepository
                .save(
                        relacaoUsuario
                );
    }


    public Optional<RelacaoUsuario> findByID(

            long id,

            String authorizationHeader

    ) {

        tokenService
                .validarAutorizacao(
                        authorizationHeader
                );


        return relacaoUsuarioRepository
                .findById(id);
    }


    public ResponseEntity<?> criar(

            RelacaoUsuario relacaoUsuario,

            String authorizationHeader

    ) {

        tokenService
                .validarAutorizacao(
                        authorizationHeader
                );


        if (
                relacaoUsuario.getId()
                        != null
        ) {

            throw new RuntimeException(
                    "Uma nova relação não deve possuir ID"
            );
        }


        return ResponseEntity.ok(

                relacaoUsuarioRepository
                        .save(
                                relacaoUsuario
                        )
        );
    }
}