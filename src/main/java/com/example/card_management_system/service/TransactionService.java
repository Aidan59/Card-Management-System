package com.example.card_management_system.service;

import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.model.Transaction;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                transaction.getId(),
                transaction.getFromCardId().getId(),
                transaction.getToCardId().getId(),
                transaction.getAmount(),
                transaction.getCreated_at().toString()
        )).collect(Collectors.toList());
    }
}
