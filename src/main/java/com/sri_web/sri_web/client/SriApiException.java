package com.sri_web.sri_web.client;

public class SriApiException extends RuntimeException {

    private final int status;

    public SriApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
