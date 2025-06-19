package com.example.card_management_system.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnknownErrorException extends RuntimeException {
    public UnknownErrorException(String message) {
        super(message);
    }
}
