package com.example.card_management_system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String message;

    private LocalDateTime timestamp;
    
}
