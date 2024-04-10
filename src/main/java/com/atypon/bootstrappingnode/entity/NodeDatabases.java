package com.atypon.bootstrappingnode.entity;

public class NodeDatabases implements Comparable<NodeDatabases>{
    private final int nodeTcpPort;
    private int nodeDatabasesCount;

    public NodeDatabases(int nodeTcpPort, int nodeDatabasesCount) {
        this.nodeTcpPort = nodeTcpPort;
        this.nodeDatabasesCount = nodeDatabasesCount;
    }

    public void incrementDatabasesCount() {
        this.nodeDatabasesCount++;
    }

    public int getNodeTcpPort() {
        return nodeTcpPort;
    }

    public int getNodeDatabasesCount() {
        return nodeDatabasesCount;
    }

    @Override
    public int compareTo(NodeDatabases o) {
        return sortByNodeDatabasesCountAscending(o);
    }

    private int sortByNodeDatabasesCountAscending(NodeDatabases o) {
        return this.nodeDatabasesCount - o.nodeDatabasesCount;
    }
}
