package com.atypon.bootstrappingnode.secuirty;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.authentication.secret}")
    private String secret;

    public String createToken(String userId) {
        Date now = new Date();
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(now)
                .sign(algorithm);
    }


    public String getUserId(HttpServletRequest request) throws JWTVerificationException {
        return getUserIdFromToken( extractToken(request) );
    }

    //-------------- Private helper methods

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length());
        }
        return null;
    }

    private String getUserIdFromToken(String token) throws JWTVerificationException {
        if (token == null) throw new JWTVerificationException("");
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
