package com.example.card_management_system.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
