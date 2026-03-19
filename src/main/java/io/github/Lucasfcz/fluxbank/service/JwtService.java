package io.github.Lucasfcz.fluxbank.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtService {

    private final String secret;

    public JwtService(@Value("${jwt.secret}")String secret) {
        this.secret = secret;
    }

    public String generateToken(JwtUser user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withClaim("userid", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(86400)) // Token expires in 24 hours
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public String validateToken(String token) {
        try{
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();}
        catch (Exception e){
            e.getMessage();
            return null;
        }
    }
}