package com.atypon.bootstrappingnode.secuirty;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationFilter implements Filter {

    private final JwtService jwtService;

    @Autowired
    public AuthenticationFilter(JwtService authenticationService) {
        this.jwtService = authenticationService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try {
            String userId = jwtService.getUserId(request);
            if (userId == null) {
                response.setStatus(401);
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);

        } catch (JWTVerificationException e) {
            response.setStatus(401);
        }
    }
}
