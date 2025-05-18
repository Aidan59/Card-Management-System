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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(Date expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
