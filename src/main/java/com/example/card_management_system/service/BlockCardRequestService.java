package com.example.card_management_system.service;

import com.example.card_management_system.exception.CardNotFoundException;
import com.example.card_management_system.exception.RequestAlreadyExistException;
import com.example.card_management_system.exception.UserNotFoundException;
import com.example.card_management_system.model.BlockCardRequest;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.BlockCardRequestRepository;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * Service responsible for handling operations related to card block requests.
 * Allows users to submit a request to block a specific card, which is then stored.
 */
@Service
public class BlockCardRequestService {

    private final BlockCardRequestRepository blockCardRequestRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new {@code BlockCardRequestService} with the necessary repositories.
     *
     * @param blockCardRequestRepository the repository used for storing block card requests
     * @param cardRepository             the repository used for accessing cards
     * @param userRepository             the repository used for accessing users
     */
    public BlockCardRequestService(BlockCardRequestRepository blockCardRequestRepository, CardRepository cardRepository, UserRepository userRepository) {
        this.blockCardRequestRepository = blockCardRequestRepository;
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates and saves a new block card request from a given user for a given card.
     * The request is marked as {@code PENDING} by default.
     *
     * @param userId the ID of the user requesting the card block
     * @param cardId the ID of the card to be blocked
     */
    public void makeCardBlockRequest(Long userId, Long cardId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));

        if (blockCardRequestRepository.findBlockCardRequestByCard_Id(cardId).isPresent()) throw new RequestAlreadyExistException();


        BlockCardRequest blockCardRequest = new BlockCardRequest();

        blockCardRequest.setCard(card);
        blockCardRequest.setUser(user);
        blockCardRequest.setRequest_date(new Timestamp(System.currentTimeMillis()));
        blockCardRequest.setStatus(BlockCardRequest.Status.PENDING);

        blockCardRequestRepository.save(blockCardRequest);
    }

    /**
     * Deletes block card request from a given card.
     *
     * @param cardId
     */
    @Transactional
    public void deleteCardBlockRequest(Long cardId) {
        blockCardRequestRepository.deleteBlockCardRequestByCard_Id(cardId);
    }
}
