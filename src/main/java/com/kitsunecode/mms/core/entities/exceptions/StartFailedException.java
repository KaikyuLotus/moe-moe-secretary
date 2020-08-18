package com.kitsunecode.mms.core.entities.exceptions;

public class StartFailedException extends RuntimeException {

    private String httpHelpUrl = null;

    public StartFailedException(String message, Throwable e) {
        super(message, e);
    }

    public StartFailedException(String message) {
        super(message);
    }

    public StartFailedException(String message, Exception ex) {
        super(message, ex);
    }

    public StartFailedException(String message, String url, Exception ex) {
        super(message, ex);
        this.httpHelpUrl = url;
    }

    public StartFailedException(String message, String url) {
        super(message);
        this.httpHelpUrl = url;
    }

    public boolean hasHelpUrl() {
        return httpHelpUrl != null;
    }

    public String getHttpHelpUrl() {
        return httpHelpUrl;
    }
}
