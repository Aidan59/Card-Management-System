package com.example.card_management_system.repository;

import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findAllByFromCardId(Card fromCardId);

    List<Transaction> findAllByToCardId(Card toCardId);
}
