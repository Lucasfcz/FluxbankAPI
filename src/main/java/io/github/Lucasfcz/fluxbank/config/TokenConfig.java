package io.github.Lucasfcz.fluxbank.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenConfig {

    private String secret = "secret";

    public String generateToken(JwtUser user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("userid", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(86400)) // Token expires in 24 hours
                .withIssuedAt(Instant.now())
                .sign(algorithm);


    }
}
