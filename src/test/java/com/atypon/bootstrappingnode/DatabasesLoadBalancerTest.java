package com.atypon.bootstrappingnode;

import com.atypon.bootstrappingnode.services.NodesLoadBalancer;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabasesLoadBalancerTest {

    private NodesLoadBalancer loadBalancer;
    private ExecutorService executor;

    @Before
    public void setUp() {
        loadBalancer = new NodesLoadBalancer();
        loadBalancer.initializeNodes(Arrays.asList(8080, 8081, 8082));
        executor = Executors.newFixedThreadPool(10);  // Adjust thread count as needed
    }

    @Test
    public void testGetNextNodePortConcurrently() throws InterruptedException {
        int threadCount = 10;
        final CountDownLatch latch = new CountDownLatch(1);
        List<Future<Integer>> results = new ArrayList<>();

        // Create and submit tasks
        for (int i = 0; i < threadCount; i++) {
            results.add(executor.submit(() -> {
                latch.await();
                return loadBalancer.getNextNodePort();
            }));
        }

        latch.countDown();  // Start all threads at once
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Collect results and verify
        for (Future<Integer> result : results) {
            try {
                int port = result.get();
                assertTrue("Port should be one of the initialized ports", Arrays.asList(8080, 8081, 8082).contains(port));
            } catch (ExecutionException e) {
                fail("Test failed with exception: " + e.getCause());
            }
        }
    }
}
