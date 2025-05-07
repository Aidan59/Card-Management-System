package com.example.card_management_system.service;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import com.example.card_management_system.util.AESUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AESUtil aesUtil;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCard_shouldEncryptAndSave() throws Exception {
        CardDto dto = new CardDto("1234123412341234", new Timestamp(System.currentTimeMillis()), "ACTIVE", new BigDecimal("100"), 1L);
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(aesUtil.encrypt("1234123412341234")).thenReturn("encrypted");
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardDto result = cardService.createCard(dto);

        assertNotNull(result);
        assertTrue(result.getNumber().startsWith("************"));
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void blockCard_shouldUpdateStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus("ACTIVE");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = cardService.blockCard(1L);

        assertEquals("BLOCKED", result.getStatus());
    }

    @Test
    void activateCard_shouldUpdateStatus() {
        Card card = new Card();
        card.setId(1L);
        card.setStatus("BLOCKED");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        Card result = cardService.activateCard(1L);

        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void deleteCard_shouldCallRepositoryDelete() {
        cardService.deleteCard(1L);
        verify(cardRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllCards_ReturnMaskedCardDtos() throws Exception {
        Card card = new Card();
        card.setNumber("encrypted");
        card.setStatus("ACTIVE");
        card.setBalance(new BigDecimal("100"));
        card.setExpiration_date(new Timestamp(System.currentTimeMillis()));
        User user = new User();
        user.setId(1L);
        card.setUser(user);

        when(cardRepository.findAll()).thenReturn(List.of(card));
        when(aesUtil.decrypt("encrypted")).thenReturn("1234123412341234");

        List<CardDto> result = cardService.getAllCards();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getNumber().startsWith("************"));
    }

    @Test
    void getAllUserCards_ReturnMaskedCardDtos() throws Exception {
        Card card = new Card();
        card.setNumber("encrypted");
        card.setStatus("ACTIVE");
        card.setBalance(new BigDecimal("100"));
        card.setExpiration_date(new Timestamp(System.currentTimeMillis()));
        User user = new User();
        user.setId(2L);
        card.setUser(user);

        when(cardRepository.findAllByUserId(2L)).thenReturn(List.of(card));
        when(aesUtil.decrypt("encrypted")).thenReturn("1234123412341234");

        List<CardDto> result = cardService.getAllUserCards(2L);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getUserId());
    }

    @Test
    void getCard_ReturnCardById() {
        Card card = new Card();
        card.setId(10L);

        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        Card result = cardService.getCard(10L);

        assertEquals(10L, result.getId());
    }
}
