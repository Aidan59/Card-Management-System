package com.example.card_management_system.exception;

import lombok.Getter;

@Getter
public class CardNotFoundException extends RuntimeException {

    private Long cardId;

    public CardNotFoundException(String message) {
        super(message);
    }

    public CardNotFoundException(Long cardId) {
        this.cardId = cardId;
    }
}
