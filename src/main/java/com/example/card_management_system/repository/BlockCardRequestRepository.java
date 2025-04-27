package com.example.card_management_system.repository;

import com.example.card_management_system.model.BlockCardRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockCardRequestRepository extends JpaRepository<BlockCardRequest,Long> {
}
