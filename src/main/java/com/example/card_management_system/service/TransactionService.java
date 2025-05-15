package com.example.card_management_system.service;

import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.model.Transaction;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.TransactionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling transactions between cards.
 * Provides functionality to execute transactions, retrieve transaction history for a specific card,
 * and calculate the total amount of money withdrawn by the user in the current month.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    /**
     * Constructs a new TransactionService with the specified repositories.
     *
     * @param transactionRepository repository for transaction persistence
     * @param cardRepository        repository for card retrieval and persistence
     */
    public TransactionService(TransactionRepository transactionRepository, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    /**
     * Retrieves all transactions associated with a specific card.
     * This includes both transactions where the card was used as a source or a destination.
     *
     * @param cardId ID of the card
     * @return list of {@link TransactionDto} representing the transactions
     */
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

    /**
     * Executes a transaction between two cards or a withdrawal if the recipient is null.
     * Automatically adjusts the balance of the involved cards and records the transaction.
     *
     * @param transactionDto object containing transaction details
     * @throws RuntimeException if the balance on the source card is insufficient
     */
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


    /**
     * Calculates the total sum of withdrawals made by the current user in the current calendar month.
     *
     * @return total sum of withdrawals as {@link BigDecimal}
     */
    public BigDecimal getMonthlyWithdrawSum() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);

        return transactionRepository.getSumOfWithdrawalsForCurrentMonthByUser(startOfMonth, startOfNextMonth, getCurrentUser().getId());
    }

    /**
     * Retrieves the current authenticated user from the security context.
     *
     * @return the currently authenticated {@link User}
     */
    private User getCurrentUser() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
