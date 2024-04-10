package com.atypon.bootstrappingnode.entity;

import java.util.ArrayList;
import java.util.List;

public class AppUser {
    private String userId;
    private String email;
    private String username;
    private String password;
    private String userRole = "STANDARD_USER";
    private String accountState = "Active";
    private final List<Database> databases = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountState() {
        return accountState;
    }

    public void setAccountState(String accountState) {
        this.accountState = accountState;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    public void addToDatabases(Database database) {
        databases.add(database);
    }

    public List<Database> getDatabases() {
        return databases;
    }
}
