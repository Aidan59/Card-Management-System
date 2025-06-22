package com.example.card_management_system.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardAlreadyExistsException extends RuntimeException {
    public CardAlreadyExistsException(String message) {
        super(message);
    }
}
