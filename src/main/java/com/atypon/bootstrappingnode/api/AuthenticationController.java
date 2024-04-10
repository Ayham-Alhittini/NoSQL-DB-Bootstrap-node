package com.atypon.bootstrappingnode.api;

import com.atypon.bootstrappingnode.dto.LoginDto;
import com.atypon.bootstrappingnode.dto.RegisterDto;
import com.atypon.bootstrappingnode.dto.UserDto;
import com.atypon.bootstrappingnode.entity.AppUser;
import com.atypon.bootstrappingnode.secuirty.JwtService;
import com.atypon.bootstrappingnode.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:4200")
public class AuthenticationController {

    private final UserManager userManager;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(UserManager userManager, JwtService jwtService) {
        this.userManager = userManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public UserDto register(@RequestBody RegisterDto registerDto) throws Exception {

        AppUser appUser = userManager.createNewUser(registerDto.userName, registerDto.email, registerDto.password);
        userManager.saveUser(appUser);
        return new UserDto(appUser, jwtService.createToken(appUser.getUserId()));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto) {

        AppUser appUser = userManager.getUserByEmail(loginDto.email);
        if (appUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        if (userManager.isPasswordMatch(appUser, loginDto.password)) {
            UserDto userDto = new UserDto(appUser, jwtService.createToken(appUser.getUserId()));
            return ResponseEntity.ok(userDto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

}
