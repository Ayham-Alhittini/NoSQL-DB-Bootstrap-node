package com.atypon.bootstrappingnode.services;

import com.atypon.bootstrappingnode.entity.NodeDatabases;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class DatabasesLoadBalancer {

    private final PriorityBlockingQueue<NodeDatabases> balancer = new PriorityBlockingQueue<>();

    public int getNextNodePort()  {
        try {
            if (balancer.isEmpty()) throw new RuntimeException("Balancer not initialized");

            NodeDatabases nodeDatabases = balancer.take();
            nodeDatabases.incrementDatabasesCount();
            balancer.add( nodeDatabases);
            return nodeDatabases.getNodeTcpPort();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeNodes(List<Integer> nodesPort) {
        for (Integer port: nodesPort)
            balancer.add( new NodeDatabases(port, 0) );
    }

}