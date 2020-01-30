package com.kitsunecode.mms.core.entities;

public class CommandOutput {

    private String stdout;
    private String stderr;
    private int exitCode;
    private boolean hasException;
    private Throwable exception;

    public CommandOutput(String stdout, String stderr, int exitCode) {
        this.stderr = stderr;
        this.stdout = stdout;
        this.exitCode = exitCode;
    }

    public CommandOutput(Throwable exception) {
        this.exception = exception;
        this.hasException = true;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public int getExitCode() {
        return exitCode;
    }

    public boolean hasException() {
        return hasException;
    }

    public Throwable getException() {
        return exception;
    }
}
