package com.example.card_management_system.service;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import com.example.card_management_system.util.AESUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing bank cards.
 * <p>
 * Handles operations such as creating, blocking, activating, deleting cards,
 * retrieving all cards, and retrieving cards by user.
 */
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final AESUtil aesUtil;

    public CardService(CardRepository cardRepository, UserRepository userRepository, AESUtil aesUtil) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.aesUtil = aesUtil;
    }

    /**
     * Creates a new card for a user.
     * The card number is encrypted before being saved,
     * and the returned DTO contains a masked version of the number.
     *
     * @param cardDto DTO containing card details to be created
     * @return CardDto with masked card number
     * @throws Exception if encryption fails
     */
    public CardDto createCard(CardDto cardDto) throws Exception {
        Card card = new Card();
        card.setUser(userRepository.findById(cardDto.getUserId()).get());
        card.setBalance(cardDto.getBalance());
        card.setNumber(aesUtil.encrypt(cardDto.getNumber()));
        card.setExpiration_date(cardDto.getExpiration_date() != null? cardDto.getExpiration_date(): new Timestamp(System.currentTimeMillis()));
        card.setStatus(cardDto.getStatus());

        cardRepository.save(card);

        cardDto.setNumber("************" + cardDto.getNumber().substring(12, 16));
        return cardDto;
    }

    /**
     * Blocks a card by setting its status to "BLOCKED".
     *
     * @param id the ID of the card to block
     * @return the updated Card object
     * @throws RuntimeException if the card is not found
     */
    public Card blockCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus("BLOCKED");
        return cardRepository.save(card);
    }

    /**
     * Activates a card by setting its status to "ACTIVE".
     *
     * @param id the ID of the card to activate
     * @return the updated Card object
     * @throws RuntimeException if the card is not found
     */
    public Card activateCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus("ACTIVE");
        return cardRepository.save(card);
    }

    /**
     * Deletes a card by its ID.
     *
     * @param id the ID of the card to delete
     */
    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

    /**
     * Retrieves all cards from the system and masks their card numbers.
     *
     * @return list of CardDto objects with masked card numbers
     */
    public List<CardDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream().map(card -> {
            try {
                return new CardDto(
                        "************" + aesUtil.decrypt(card.getNumber()).substring(12, 16),
                        card.getExpiration_date(),
                        card.getStatus().toString(),
                        card.getBalance(),
                        card.getUser().getId()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());


    }

    /**
     * Retrieves all cards that belong to a specific user and masks their card numbers.
     *
     * @param userId the ID of the user
     * @return list of CardDto objects for the user
     */
    public List<CardDto> getAllUserCards(Long userId) {
        List<Card> cards = cardRepository.findAllByUserId(userId);

        return cards.stream().map(card -> {
            try {
                return new CardDto(
                        "************" + aesUtil.decrypt(card.getNumber()).substring(12, 16),
                        card.getExpiration_date(),
                        card.getStatus(),
                        card.getBalance(),
                        card.getUser().getId()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves a card by its ID.
     *
     * @param id the ID of the card
     * @return the Card object
     */
    public Optional<Card> getCard(Long id) {
        return cardRepository.findById(id);
    }
}
