package com.example.card_management_system.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

    private Long userId;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        this.userId = userId;
    }

}
