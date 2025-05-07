package com.example.card_management_system.service;

import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.Transaction;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        var authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        org.springframework.security.core.context.SecurityContext context = mock(org.springframework.security.core.context.SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);
        when(mockUser.getId()).thenReturn(1L);
    }

    @Test
    void getAllTransactionsByCardId_returnMappedDtos() {
        Card card = new Card();
        card.setId(1L);

        Transaction transaction1 = new Transaction();
        transaction1.setFromCardId(card);
        transaction1.setToCardId(card);
        transaction1.setAmount(new BigDecimal("100"));
        transaction1.setCreated_at(new Timestamp(System.currentTimeMillis()));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(transactionRepository.findAllByFromCardId(card)).thenReturn(List.of(transaction1));
        when(transactionRepository.findAllByToCardId(card)).thenReturn(List.of());

        List<TransactionDto> result = transactionService.getAllTransactionsByCardId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getFromCardId());
    }

    @Test
    void executeTransaction_shouldTransferMoneyBetweenCards() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("200"));

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(new BigDecimal("50"));

        TransactionDto dto = new TransactionDto(1L, 2L, new BigDecimal("100"), null);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        transactionService.executeTransaction(dto);

        assertEquals(new BigDecimal("100"), fromCard.getBalance());
        assertEquals(new BigDecimal("150"), toCard.getBalance());
    }

    @Test
    void executeTransaction_shouldWithdrawIfToCardNull() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("300"));

        TransactionDto dto = new TransactionDto(1L, null, new BigDecimal("50"), null);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        transactionService.executeTransaction(dto);

        assertEquals(new BigDecimal("250"), fromCard.getBalance());
    }

    @Test
    void executeTransaction_shouldThrowIfInsufficientFunds() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(new BigDecimal("30"));

        Card toCard = new Card();
        toCard.setId(2L);

        TransactionDto dto = new TransactionDto(1L, 2L, new BigDecimal("50"), null);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.executeTransaction(dto);
        });

        assertEquals("Недостаточно средств", exception.getMessage());
    }

    @Test
    void getMonthlyWithdrawSum_shouldReturnCorrectSum() {
        when(transactionRepository.getSumOfWithdrawalsForCurrentMonthByUser(any(), any(), eq(1L)))
                .thenReturn(new BigDecimal("300"));

        BigDecimal result = transactionService.getMonthlyWithdrawSum();

        assertEquals(new BigDecimal("300"), result);
    }
}
