package com.example.card_management_system.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "from_card_id", referencedColumnName = "id", nullable = false)
    private Card fromCardId;

    @OneToOne
    @JoinColumn(name = "to_card_id", referencedColumnName = "id", nullable = false)
    private Card toCardId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Timestamp created_at;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Card getFromCardId() {
        return fromCardId;
    }

    public void setFromCardId(Card fromCardId) {
        this.fromCardId = fromCardId;
    }

    public Card getToCardId() {
        return toCardId;
    }

    public void setToCardId(Card toCardId) {
        this.toCardId = toCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
