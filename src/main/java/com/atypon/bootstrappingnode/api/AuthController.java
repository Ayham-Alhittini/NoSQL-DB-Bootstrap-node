package com.atypon.bootstrappingnode.api;

import com.atypon.bootstrappingnode.dto.RegisterDto;
import com.atypon.bootstrappingnode.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtService jwtService;

    @Autowired
    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        String userId = UUID.randomUUID().toString();
        String token = jwtService.createToken(userId, registerDto.username, registerDto.email);
        String jsonResponse = String.format("""
                {
                    "token": "%s",
                    "requestUrl": "localhost:8081"
                }
                """, token);

        return ResponseEntity.ok(jsonResponse);
    }

}
