package com.atypon.bootstrappingnode.services;

import com.atypon.bootstrappingnode.entity.AppUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserManager {

    public AppUser getUserById(String userId) {
        try {
            String userFilePath = "./users/" + userId + ".json";
            Path path = Paths.get(userFilePath);

            if (Files.isRegularFile(path)) {
                String jsonContent = Files.readString(path);
                return new ObjectMapper().readValue(jsonContent, AppUser.class);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AppUser getUserByEmail(String email) {
        try {
            String userId = DataEncryptor.encrypt(email);
            return getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AppUser createNewUser(String username, String email, String password) throws Exception {
        AppUser appUser = new AppUser();
        appUser.setUserId(DataEncryptor.encrypt(email));
        appUser.setUsername(username);
        appUser.setEmail(email);
        appUser.setPassword(DataEncryptor.encrypt(password));
        return appUser;
    }

    public void saveUser(AppUser user) throws IOException {
        String json = new ObjectMapper().valueToTree(user).toPrettyString();
        try (FileWriter file = new FileWriter("./users/" + user.getUserId() + ".json")) {
            file.write(json);
            file.flush();
        }
    }

    public boolean isPasswordMatch(AppUser appUser, String password) {
        try {
            String encryptedPassword = DataEncryptor.encrypt(password);
            return appUser.getPassword().equals(encryptedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
