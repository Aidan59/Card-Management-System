package com.example.card_management_system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class TransactionDto {

    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private Timestamp createdAt;

    public TransactionDto(Long fromCardId, Long toCardId, BigDecimal amount, Timestamp createdAt) {
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.amount = amount;
        this.createdAt = createdAt;
    }
}
