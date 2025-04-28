package com.example.card_management_system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CardDto {

    private String number;
    private Date expiration_date;
    private String status;
    private BigDecimal balance;
    private Long userId;

    public CardDto(String number, Date expiration_date, String status, BigDecimal balance, Long userId) {
        this.number = number;
        this.expiration_date = expiration_date;
        this.status = status;
        this.balance = balance;
        this.userId = userId;
    }
}
