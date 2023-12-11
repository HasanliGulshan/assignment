package com.justlife.assignment.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotWorkingException extends RuntimeException{
    private final String errorMessage;

    public NotWorkingException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
