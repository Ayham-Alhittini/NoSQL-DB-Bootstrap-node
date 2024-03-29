package com.atypon.bootstrappingnode.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SystemCommandExecutor {
    public void exec(String command) throws Exception {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        List<String> commands = new ArrayList<>();
        if (isWindows) {
            commands.add("cmd.exe");
            commands.add("/c");
        } else {
            commands.add("sh");
            commands.add("-c");
        }
        commands.add(command); // Add the entire command as a single element

        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true); // Redirects stderr to stdout
        Process process = builder.start();

        // Read output (stdout)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Log output line by line
            }
        }

        int exitCode = process.waitFor(); // Wait for the command to finish
        if (exitCode != 0) {
            throw new IOException("Command executed with error. Exit code: " + exitCode);
        }
    }
}
