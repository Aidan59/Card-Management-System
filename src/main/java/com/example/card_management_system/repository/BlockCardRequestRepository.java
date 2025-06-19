package com.example.card_management_system.repository;

import com.example.card_management_system.model.BlockCardRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlockCardRequestRepository extends JpaRepository<BlockCardRequest,Long> {
    Optional<BlockCardRequest> findBlockCardRequestByCard_Id(Long cardId);

    void deleteBlockCardRequestByCard_Id(Long cardId);
}
