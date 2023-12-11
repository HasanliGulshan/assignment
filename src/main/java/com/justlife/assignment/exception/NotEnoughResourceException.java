package com.justlife.assignment.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotEnoughResourceException extends RuntimeException {
    private String errorMessage;

    public NotEnoughResourceException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
