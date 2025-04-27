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
    private Card from_card_id;

    @OneToOne
    @JoinColumn(name = "to_card_id", referencedColumnName = "id", nullable = false)
    private Card to_card_id;

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

    public Card getFrom_card_id() {
        return from_card_id;
    }

    public void setFrom_card_id(Card from_card_id) {
        this.from_card_id = from_card_id;
    }

    public Card getTo_card_id() {
        return to_card_id;
    }

    public void setTo_card_id(Card to_card_id) {
        this.to_card_id = to_card_id;
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
