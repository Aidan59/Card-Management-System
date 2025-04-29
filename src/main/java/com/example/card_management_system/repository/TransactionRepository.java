package com.example.card_management_system.repository;

import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findAllByFromCardId(Card fromCardId);

    List<Transaction> findAllByToCardId(Card toCardId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.toCardId IS NULL " +
            "AND t.created_at >= :startOfMonth " +
            "AND t.created_at < :startOfNextMonth " +
            "AND t.user.id = :userId")
    BigDecimal getSumOfWithdrawalsForCurrentMonthByUser(@Param("startOfMonth") LocalDateTime startOfMonth,
                                                        @Param("startOfNextMonth") LocalDateTime startOfNextMonth,
                                                        @Param("userId") Long userId);
}
