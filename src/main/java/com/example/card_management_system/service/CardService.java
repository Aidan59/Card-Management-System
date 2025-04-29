package com.example.card_management_system.service;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import com.example.card_management_system.util.AESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    @Autowired
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final AESUtil aesUtil;

    public CardService(CardRepository cardRepository, UserRepository userRepository, AESUtil aesUtil) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.aesUtil = aesUtil;
    }

    public CardDto createCard(CardDto cardDto) throws Exception {
        Card card = new Card();
        card.setUser(userRepository.findById(cardDto.getUserId()).get());
        card.setBalance(cardDto.getBalance());
        card.setNumber(aesUtil.encrypt(cardDto.getNumber()));
        card.setExpiration_date(cardDto.getExpiration_date());
        card.setStatus(cardDto.getStatus());

        cardRepository.save(card);

        cardDto.setNumber("************" + cardDto.getNumber().substring(12, 16));
        return cardDto;
    }

    public Card blockCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus("BLOCKED");
        return cardRepository.save(card);
    }

    public Card activateCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new RuntimeException("Карта не найдена"));
        card.setStatus("ACTIVE");
        return cardRepository.save(card);
    }

    public void deleteCard(Long id) {
        cardRepository.deleteById(id);
    }

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

    public Card getCard(Long id) {
        return cardRepository.findById(id).get();
    }
}
