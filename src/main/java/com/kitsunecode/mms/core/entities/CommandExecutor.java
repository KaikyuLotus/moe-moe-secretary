package com.kitsunecode.mms.core.entities;

import com.kitsunecode.mms.core.entities.CommandOutput;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {

    public CommandOutput executeCommand(String baseCommand,
                                        String... params) {
        return executeCommand(baseCommand, Arrays.asList(params), null, null);
    }

    public CommandOutput executeCommand(String baseCommand,
                              List<String> params,
                              File workingDir,
                              Map<String, String> env) {
        Process process = null;
        try {

            String command = baseCommand + " " + String.join(" ", params);

            Map<String, String> defaultEnv = new HashMap<>(System.getenv());
            if (env != null) {
                defaultEnv.putAll(env); // Add the current system env
            }

            String[] envStrings = defaultEnv.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toArray(String[]::new);

            process = Runtime.getRuntime().exec(command, envStrings, workingDir);

            String stdout = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            String stderr = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);

            process.waitFor();
            int statusCode = process.exitValue();
            return new CommandOutput(stdout, stderr, statusCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            if (process != null) {
                process.destroy();
            }
            return new CommandOutput(e);
        }
    }

}
