package com.atypon.bootstrappingnode.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    public String createToken(String userId, String username, String email) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withSubject(userId)
                .withClaim("username", username)
                .withClaim("email", email)
                .withIssuedAt(now)
                .sign(algorithm);
    }


    public String getUserId(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
