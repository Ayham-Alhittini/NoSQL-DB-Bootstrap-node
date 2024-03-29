package com.atypon.bootstrappingnode.api;

import com.atypon.bootstrappingnode.dto.NodeConfigurationDto;
import com.atypon.bootstrappingnode.util.SystemCommandExecutor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

//TODO: this controller should only be accessible by admins user

@RestController
@RequestMapping("/api/cluster")
@CrossOrigin("*")
public class ClusterController {

    private final int currentNumberOfNodes = 2;

    @PostMapping("/start")
    public void start(HttpServletRequest request) throws Exception {

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor();

        for (int nodeNumber = 1; nodeNumber <= currentNumberOfNodes; nodeNumber++) {
            int portNumber = 8080 + nodeNumber;
            String startCommand = String
                    .format("docker run -d -p %d:8081 --name Node-%d --network cluster worker-node", portNumber, nodeNumber);
            commandExecutor.exec(startCommand);
        }
        initializeNodes(request);
    }

    private void initializeNodes(HttpServletRequest request) throws InterruptedException {
        Thread.sleep(10000); // wait until nodes are ready to receive requests

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));


        for (Integer currentPort: getPorts()) {
            NodeConfigurationDto nodeConfiguration = new NodeConfigurationDto();

            nodeConfiguration.currentNodePort = currentPort;
            nodeConfiguration.otherNodesPort = getPorts().stream().filter(p -> p != currentPort).toList();

            HttpEntity<NodeConfigurationDto> requestEntity = new HttpEntity<>(nodeConfiguration, headers);

            String nodeAddress = String.format("http://host.docker.internal:%d/internal/api/bootstrap/initializeNode", currentPort);

            restTemplate.exchange(nodeAddress, HttpMethod.POST, requestEntity, Void.class);
        }


    }
    private List<Integer> getPorts() {
        List<Integer> ports = new ArrayList<>();
        for (int nodePort = 8081; nodePort <= currentNumberOfNodes + 8080; nodePort++)
            ports.add(nodePort);
        return ports;
    }



    @PostMapping("/shutdown")
    public void shutdown() throws Exception {
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor();

        for (int nodeNumber = 1; nodeNumber <= currentNumberOfNodes; nodeNumber++) {
            String stopContainer = String.format("docker stop Node-%d && docker rm Node-%d", nodeNumber, nodeNumber);
            commandExecutor.exec(stopContainer);
        }
    }

}