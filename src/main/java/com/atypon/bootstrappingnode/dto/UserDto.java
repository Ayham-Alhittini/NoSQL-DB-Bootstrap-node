package com.atypon.bootstrappingnode.dto;

import com.atypon.bootstrappingnode.entity.AppUser;

public class UserDto {
    public String userId;
    public String email;
    public String userName;
    public String userRole;
    public String token;

    public UserDto(AppUser appUser, String token) {
        this.token = token;

        this.userId = appUser.getUserId();
        this.email = appUser.getEmail();
        this.userName = appUser.getUsername();
        this.userRole = appUser.getUserRole();
    }
}
