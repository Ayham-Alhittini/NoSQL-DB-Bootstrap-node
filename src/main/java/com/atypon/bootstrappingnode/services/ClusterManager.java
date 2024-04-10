package com.atypon.bootstrappingnode.services;

import com.atypon.bootstrappingnode.dto.NodeConfigurationDto;
import com.atypon.bootstrappingnode.util.SystemCommandExecutor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClusterManager {

    private final DatabasesLoadBalancer loadBalancer;
    @Autowired
    public ClusterManager(DatabasesLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }


    public void startCluster(int nodesCount) throws Exception {
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor();
        for (int nodeNumber = 1; nodeNumber <= nodesCount; nodeNumber++) {
            int portNumber = 8080 + nodeNumber;
            String startCommand = String
                    .format("docker run -d -p %d:8081 --name Node-%d --network cluster worker-node", portNumber, nodeNumber);
            commandExecutor.exec(startCommand);
        }
    }

    public void shutDownCluster(int nodesCount) throws Exception {
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor();
        for (int nodeNumber = 1; nodeNumber <= nodesCount; nodeNumber++) {
            String stopContainer = String.format("docker stop Node-%d && docker rm Node-%d", nodeNumber, nodeNumber);
            commandExecutor.exec(stopContainer);
        }
    }



    public void configureNodes(HttpServletRequest request, int nodesCount) throws InterruptedException {
        Thread.sleep(10000); // wait until nodes are ready to receive requests

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        for (Integer currentPort: getPorts(nodesCount)) {
            NodeConfigurationDto nodeConfiguration = new NodeConfigurationDto();

            nodeConfiguration.currentNodePort = currentPort;
            nodeConfiguration.otherNodesPort = getPorts(nodesCount).stream().filter(p -> p.intValue() != currentPort).toList();

            HttpEntity<NodeConfigurationDto> requestEntity = new HttpEntity<>(nodeConfiguration, headers);

            String nodeAddress = String.format("http://host.docker.internal:%d/internal/api/bootstrap/initializeNode", currentPort);

            restTemplate.exchange(nodeAddress, HttpMethod.POST, requestEntity, Void.class);
        }
        loadBalancer.initializeNodes( getPorts(nodesCount) );
    }

    private List<Integer> getPorts(int nodesCount) {
        List<Integer> ports = new ArrayList<>();
        for (int nodePort = 8081; nodePort <= nodesCount + 8080; nodePort++)
            ports.add(nodePort);
        return ports;
    }

}
