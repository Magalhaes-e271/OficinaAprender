package br.sp.etec.sebrae.OficinaAprender.Service.Token;
import br.sp.etec.sebrae.OficinaAprender.Entity.Usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {

    private static final String SECRET = "umnumeroaleatoriolegal";
    private static final String ISSUER = "OficinaAprender";

    public String gerarToken(Usuario usuario) {

        Algorithm algoritmo = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getEmail())
                .withClaim("id", usuario.getId())

                // Troquei de 30 segundos para 2 horas
                .withExpiresAt(
                        Instant.now().plus(2, ChronoUnit.HOURS)
                )

                .sign(algoritmo);
    }
    public String extrairEmail(String token) {

        try {

            Algorithm algoritmo =
                    Algorithm.HMAC256(SECRET);

            return JWT.require(algoritmo)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException erro) {

            return null;
        }
    }

    public String validarToken(String token) {

        try {

            Algorithm algoritmo = Algorithm.HMAC256(SECRET);

            return JWT.require(algoritmo)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException erro) {

            return null;
        }
    }


    public String validarAutorizacao(
            String authorizationHeader
    ) {

        if (
                authorizationHeader == null ||
                        authorizationHeader.isBlank()
        ) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token não informado"
            );
        }


        if (
                !authorizationHeader.regionMatches(
                        true,
                        0,
                        "Bearer ",
                        0,
                        7
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Formato do token inválido"
            );
        }


        String token =
                authorizationHeader
                        .substring(7)
                        .trim();


        if (token.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token não informado"
            );
        }


        String email = validarToken(token);


        if (email == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token inválido ou expirado"
            );
        }


        return email;
    }
}