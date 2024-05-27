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
import java.util.concurrent.*;

@Service
public class ClusterManager {

    private final NodesLoadBalancer loadBalancer;
    @Autowired
    public ClusterManager(NodesLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }


    public void createNodes(int nodesCount) throws Exception {
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
        // Wait until nodes are ready to receive requests
        Thread.sleep(10000);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders(request);

        ExecutorService executorService = Executors.newFixedThreadPool(nodesCount);
        List<Callable<Boolean>> tasks = createConfigurationTasks(nodesCount, restTemplate, headers);

        executeTasks(executorService, tasks);
        loadBalancer.initializeNodes(getPorts(nodesCount));
    }

    private HttpHeaders createHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));
        return headers;
    }

    private List<Callable<Boolean>> createConfigurationTasks(int nodesCount, RestTemplate restTemplate, HttpHeaders headers) {
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (Integer currentPort : getPorts(nodesCount)) {
            tasks.add(() -> configureNode(currentPort, nodesCount, restTemplate, headers));
        }
        return tasks;
    }

    private Boolean configureNode(int currentPort, int nodesCount, RestTemplate restTemplate, HttpHeaders headers) throws InterruptedException {
        NodeConfigurationDto nodeConfiguration = new NodeConfigurationDto();
        nodeConfiguration.currentNodePort = currentPort;
        nodeConfiguration.otherNodesPort = getPorts(nodesCount).stream()
                .filter(p -> p != currentPort)
                .toList();

        HttpEntity<NodeConfigurationDto> requestEntity = new HttpEntity<>(nodeConfiguration, headers);
        String nodeAddress = String.format("http://host.docker.internal:%d/internal/api/bootstrap/initializeNode", currentPort);

        boolean configured = false;
        while (!configured) {
            try {
                restTemplate.exchange(nodeAddress, HttpMethod.POST, requestEntity, Void.class);
                configured = true;
            } catch (Exception ignored) {
                Thread.sleep(500);
            }
        }
        return true;
    }

    private void executeTasks(ExecutorService executorService, List<Callable<Boolean>> tasks) throws InterruptedException {
        try {
            List<Future<Boolean>> results = executorService.invokeAll(tasks);
            for (Future<Boolean> result : results) {
                result.get(); // waits for the task to complete
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

    private List<Integer> getPorts(int nodesCount) {
        List<Integer> ports = new ArrayList<>();
        for (int nodePort = 8081; nodePort <= nodesCount + 8080; nodePort++)
            ports.add(nodePort);
        return ports;
    }

}
