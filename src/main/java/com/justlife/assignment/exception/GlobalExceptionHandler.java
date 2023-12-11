package com.justlife.assignment.exception;

import com.justlife.assignment.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException e) {
        return ResponseEntity.ok(getErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.ok(getErrorResponse(e.getMessage()));
    }


    @ExceptionHandler(value = NotWorkingException.class)
    public ResponseEntity<ErrorResponse> handleNotWorkingException(NotWorkingException e) {
        return ResponseEntity.ok(getErrorResponse(e.getErrorMessage()));
    }

    @ExceptionHandler(value = NotEnoughResourceException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughResourceException(NotEnoughResourceException e) {
        return ResponseEntity.ok(getErrorResponse(e.getErrorMessage()));
    }

    private ErrorResponse getErrorResponse(String errorMessage) {
        return ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .errorMessage(errorMessage)
                .build();
    }
}
