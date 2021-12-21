package com.sky.security.service.exceptions;

public class ErrorDto {

    private int downstreamStatusCode;
    private String errorCode;
    private String message;

    public ErrorDto() {
    }

    public ErrorDto(int downstreamStatusCode, String errorCode, String message) {
        this.downstreamStatusCode = downstreamStatusCode;
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getDownstreamStatusCode() {
        return downstreamStatusCode;
    }

    public void setDownstreamStatusCode(int downstreamStatusCode) {
        this.downstreamStatusCode = downstreamStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
