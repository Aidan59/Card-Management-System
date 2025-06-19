package com.example.card_management_system.repository;

import com.example.card_management_system.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findAllByUserId(Long userId);

    Optional<Card> findCardById(Long id);

    Card findTopByOrderByIdDesc();
}
