package com.example.card_management_system.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RequestAlreadyExistException extends RuntimeException{
    public RequestAlreadyExistException(String message) {
        super(message);
    }
}
