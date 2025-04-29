package com.example.card_management_system.service;

import com.example.card_management_system.model.BlockCardRequest;
import com.example.card_management_system.repository.BlockCardRequestRepository;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class BlockCardRequestService {

    private final BlockCardRequestRepository blockCardRequestRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public BlockCardRequestService(BlockCardRequestRepository blockCardRequestRepository, CardRepository cardRepository, UserRepository userRepository) {
        this.blockCardRequestRepository = blockCardRequestRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    public void makeCardBlockRequest(Long userId, Long cardId) {
        BlockCardRequest blockCardRequest = new BlockCardRequest();
        blockCardRequest.setCard(cardRepository.findById(cardId).get());
        blockCardRequest.setUser(userRepository.findById(userId).get());
        blockCardRequest.setRequest_date(new Timestamp(System.currentTimeMillis()));
        blockCardRequest.setStatus(BlockCardRequest.Status.PENDING);

        blockCardRequestRepository.save(blockCardRequest);
    }
}
