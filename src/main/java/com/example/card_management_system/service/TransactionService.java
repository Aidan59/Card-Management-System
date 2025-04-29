package com.example.card_management_system.service;

import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.Transaction;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    public TransactionService(TransactionRepository transactionRepository, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    public List<TransactionDto> getAllTransactionsByCardId(Long cardId) {
        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(transactionRepository.findAllByFromCardId(cardRepository.findById(cardId).get()));
        transactions.addAll(transactionRepository.findAllByToCardId(cardRepository.findById(cardId).get()));

        return transactions.stream().map(transaction -> new TransactionDto(
                transaction.getFromCardId().getId(),
                transaction.getToCardId() != null ? transaction.getToCardId().getId() : null,
                transaction.getAmount(),
                transaction.getCreated_at()
        )).collect(Collectors.toList());
    }

    public void executeTransaction(TransactionDto transactionDto) {
        Card fromCard = cardRepository.findById(transactionDto.getFromCardId()).get();
        Card toCard;
        if (transactionDto.getToCardId() == null){
            toCard = null;
        } else {
            toCard = cardRepository.findById(transactionDto.getToCardId()).get();
        }

        if (fromCard.getBalance().compareTo(transactionDto.getAmount()) < 0 && toCard != null) {
            throw new RuntimeException("Недостаточно средств");
        } else if (toCard == null) {
            fromCard.setBalance(fromCard.getBalance().subtract(transactionDto.getAmount()));
            cardRepository.save(fromCard);
        } else {
            fromCard.setBalance(fromCard.getBalance().subtract(transactionDto.getAmount()));
            toCard.setBalance(toCard.getBalance().add(transactionDto.getAmount()));
            cardRepository.save(toCard);
            cardRepository.save(fromCard);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(getCurrentUser());
        transaction.setFromCardId(fromCard);
        transaction.setToCardId(toCard);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setCreated_at(transactionDto.getCreatedAt() == null? new Timestamp(System.currentTimeMillis()): transactionDto.getCreatedAt());

        transactionRepository.save(transaction);
    }

    public BigDecimal getMonthlyWithdrawSum() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        return transactionRepository.getSumOfWithdrawalsForCurrentMonthByUser(startOfMonth, startOfNextMonth, getCurrentUser().getId());
    }

    private User getCurrentUser() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
