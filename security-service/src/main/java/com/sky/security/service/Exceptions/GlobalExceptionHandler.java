package com.sky.security.service.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = DownstreamException.class)
    public ResponseEntity<ErrorDto> handleDownstreamException(DownstreamException downstreamException) {
        ErrorDto errorDto = new ErrorDto(500, "VR101", downstreamException.getMessage());
        return ResponseEntity.status(500).body(errorDto);
    }
}
