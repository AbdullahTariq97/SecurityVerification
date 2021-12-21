package com.sky.security.service.exceptions;

public class DownstreamException extends RuntimeException {

    private int statusCode;

    public DownstreamException(int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }

    public DownstreamException(String message){
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
