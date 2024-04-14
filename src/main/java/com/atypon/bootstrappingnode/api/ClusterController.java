package com.atypon.bootstrappingnode.api;

import com.atypon.bootstrappingnode.secuirty.AuthorizationService;
import com.atypon.bootstrappingnode.services.ClusterManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/cluster")
@CrossOrigin(origins = "http://localhost:4200")
public class ClusterController {

    private boolean isClusterRunning = false;
    private final int currentNumberOfNodes = 2;
    private final ClusterManager clusterManager;
    private final AuthorizationService authorizationService;

    @Autowired
    public ClusterController(ClusterManager clusterManager, AuthorizationService authorizationService) {
        this.clusterManager = clusterManager;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/start")
    public void start(HttpServletRequest request) throws Exception {
        authorizationService.validateAdminAuthority(request);
        clusterManager.startCluster(currentNumberOfNodes);
        clusterManager.configureNodes(request, currentNumberOfNodes);
        isClusterRunning = true;
    }

    @PostMapping("/shutdown")
    public void shutdown(HttpServletRequest request) throws Exception {
        authorizationService.validateAdminAuthority(request);
        clusterManager.shutDownCluster(currentNumberOfNodes);
        isClusterRunning = false;
    }

    @GetMapping("clusterRunningStatus")
    public JsonNode getClusterRunningStatus(HttpServletRequest request) {
        authorizationService.validateAdminAuthority(request);
        return new ObjectMapper().createObjectNode().put("clusterRunningStatus", isClusterRunning);
    }

}