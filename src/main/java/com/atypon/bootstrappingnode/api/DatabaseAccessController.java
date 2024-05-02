package com.atypon.bootstrappingnode.api;

import com.atypon.bootstrappingnode.dto.NodeConfigurationDto;
import com.atypon.bootstrappingnode.entity.AppUser;
import com.atypon.bootstrappingnode.entity.Database;
import com.atypon.bootstrappingnode.secuirty.JwtService;
import com.atypon.bootstrappingnode.util.DataEncryptor;
import com.atypon.bootstrappingnode.services.NodesLoadBalancer;
import com.atypon.bootstrappingnode.services.UserManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/access")
@CrossOrigin(origins = "http://localhost:4200")
public class DatabaseAccessController {

    private final UserManager userManager;
    private final NodesLoadBalancer loadBalancer;
    private final JwtService authenticationService;

    @Autowired
    public DatabaseAccessController(UserManager userManager, NodesLoadBalancer loadBalancer, JwtService authenticationService) {
        this.userManager = userManager;
        this.loadBalancer = loadBalancer;
        this.authenticationService = authenticationService;
    }


    @PostMapping("createDB/{dbName}")
    public ResponseEntity<Database> createDB(HttpServletRequest request, @PathVariable String dbName) throws Exception {

        int nodePort = loadBalancer.getNextNodePort();
        // In case user not found an unauthorized exception will occur
        String userId = authenticationService.getUserId(request);
        String apiKey = getApiKey(dbName, nodePort, userId);
        Database database = new Database(dbName, apiKey);
        AppUser appUser = userManager.getUserById(userId);
        appUser.addToDatabases(database);
        createDatabaseOnAssignedNode(request, nodePort, dbName);
        userManager.saveUser(appUser);
        return ResponseEntity.ok(database);
    }

    @GetMapping("showDbs")
    public List<Database> showDbs(HttpServletRequest request) {
        AppUser appUser = userManager.getUserById(authenticationService.getUserId(request));
        return appUser.getDatabases();
    }

    private String getApiKey(String database, int nodePort, String originator) throws Exception {
        String nodeAddress = String.format("http://localhost:%d", nodePort);
        return DataEncryptor.encrypt(nodeAddress, database, originator);
    }

    private void createDatabaseOnAssignedNode(HttpServletRequest request, int assignedNode, String database) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));
        HttpEntity<NodeConfigurationDto> requestEntity = new HttpEntity<>(headers);
        String requestUrl = String.format("http://localhost:%d/api/database/createDB/%s", assignedNode, database);
        restTemplate.exchange(requestUrl, HttpMethod.POST, requestEntity, Void.class);
    }
}
