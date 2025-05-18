package com.example.card_management_system.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Getter
@Setter
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

    public Long getFromCardId() {
        return fromCardId;
    }

    public void setFromCardId(Long fromCardId) {
        this.fromCardId = fromCardId;
    }

    public Long getToCardId() {
        return toCardId;
    }

    public void setToCardId(Long toCardId) {
        this.toCardId = toCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
