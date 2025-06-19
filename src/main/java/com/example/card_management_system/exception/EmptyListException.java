package com.example.card_management_system.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmptyListException extends RuntimeException {
    public EmptyListException(String message) {
        super(message);
    }
}
