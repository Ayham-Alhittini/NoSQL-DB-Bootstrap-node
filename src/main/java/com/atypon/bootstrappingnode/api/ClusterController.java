package com.atypon.bootstrappingnode.api;

import com.atypon.bootstrappingnode.services.ClusterManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/cluster")
@CrossOrigin("http://localhost:4200")
public class ClusterController {

    private boolean isClusterRunning = false;
    private final int currentNumberOfNodes = 2;
    private final ClusterManager clusterManager;

    @Autowired
    public ClusterController(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @PostMapping("/start")
    public void start(HttpServletRequest request) throws Exception {

        if (isClusterRunning) return;
        clusterManager.startCluster(currentNumberOfNodes);
        clusterManager.configureNodes(request, currentNumberOfNodes);
        isClusterRunning = true;
    }

    @PostMapping("/shutdown")
    public void shutdown() throws Exception {

        if (!isClusterRunning) return;
        clusterManager.shutDownCluster(currentNumberOfNodes);
        isClusterRunning = false;
    }

    @GetMapping("clusterRunningStatus")
    public JsonNode getClusterRunningStatus() {
        return new ObjectMapper().createObjectNode().put("clusterRunningStatus", isClusterRunning);
    }

}