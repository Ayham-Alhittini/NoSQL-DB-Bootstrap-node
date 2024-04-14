package com.atypon.bootstrappingnode.secuirty;

import com.atypon.bootstrappingnode.entity.AppUser;
import com.atypon.bootstrappingnode.exceptions.AdminAuthorityException;
import com.atypon.bootstrappingnode.services.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    private final JwtService jwtService;
    private final UserManager userManager;

    @Autowired
    public AuthorizationService(JwtService jwtService, UserManager userManager) {
        this.jwtService = jwtService;
        this.userManager = userManager;
    }

    public void validateAdminAuthority(HttpServletRequest request) {
        String userId = jwtService.getUserId(request);
        AppUser appUser = userManager.getUserById(userId);

        if (!"ADMIN".equals(appUser.getUserRole())) {
            throw new AdminAuthorityException();
        }
    }

}
