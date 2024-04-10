package com.atypon.bootstrappingnode.secuirty;

import com.atypon.bootstrappingnode.entity.AppUser;
import com.atypon.bootstrappingnode.services.UserManager;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthorizationFilter implements Filter {
    private final JwtService jwtService;
    private final UserManager userManager;
    @Autowired
    public AuthorizationFilter(JwtService authenticationService, UserManager userManager) {
        this.jwtService = authenticationService;
        this.userManager = userManager;
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
            AppUser appUser = userManager.getUserById(userId);
            if (appUser == null) {
                response.setStatus(401);
                return;
            }

            if (!"ADMIN".equals(appUser.getUserRole())) {
                response.setStatus(403);
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);

        } catch (JWTVerificationException e) {
            response.setStatus(401);
        }
    }
}
