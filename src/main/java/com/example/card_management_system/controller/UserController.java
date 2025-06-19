package com.example.card_management_system.controller;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.exception.CardNotFoundException;
import com.example.card_management_system.exception.EmptyListException;
import com.example.card_management_system.exception.ForbiddenOperationException;
import com.example.card_management_system.exception.UnknownErrorException;
import com.example.card_management_system.model.User;
import com.example.card_management_system.service.BlockCardRequestService;
import com.example.card_management_system.service.CardService;
import com.example.card_management_system.service.TransactionService;
import com.example.card_management_system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for handling user-related operations such as retrieving cards,
 * submitting block requests, executing transactions, and withdrawing funds.
 *
 * <p>All endpoints are prefixed with <code>/api/user/</code> and require authentication.
 */
@RestController
@RequestMapping("/api/user/")
public class
UserController {

    /**
     * Service for managing users.
     */
    private final UserService userService;
    /**
     * Service for managing cards.
     */
    private final CardService cardService;
    /**
     * Service for handling card block requests.
     */
    private final BlockCardRequestService blockCardRequestService;
    /**
     * Service for processing transactions.
     */
    private final TransactionService transactionService;
    /**
     * Service for loading user details.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Constructs the UserController with all required services.
     *
     * @param userService              the user service
     * @param cardService              the card service
     * @param blockCardRequestService  the block card request service
     * @param transactionService       the transaction service
     * @param userDetailsService       the user details service
     */
    public UserController(UserService userService, CardService cardService, BlockCardRequestService blockCardRequestService, TransactionService transactionService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.cardService = cardService;
        this.blockCardRequestService = blockCardRequestService;
        this.transactionService = transactionService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Retrieves all cards owned by the currently authenticated user.
     *
     * @return list of user's cards
     */
    @GetMapping("/cards")
    public List<CardDto> getAllUserCards(){
        List<CardDto> cardDtos = cardService.getAllUserCards(getCurrentUserId());

        if (cardDtos.isEmpty()) throw new EmptyListException();

        return cardDtos;
    }

    /**
     * Submits a request to block a card.
     *
     * @param id the ID of the card to block
     * @return confirmation message
     */
    @PostMapping("/cards/{id}/block_request")
    public ResponseEntity<String> getBlockCardRequest(@PathVariable Long id){

        blockCardRequestService.makeCardBlockRequest(getCurrentUserId(), id);

        return ResponseEntity.ok("The request to block the card has been submitted.");
    }

    /**
     * Retrieves all transactions for a specific card if the card belongs to the current user.
     *
     * @param id the card ID
     * @return list of transactions, or null if unauthorized
     */
    @GetMapping("/cards/{id}/transactions")
    public List<TransactionDto> getAllCardTransactions(@PathVariable Long id){
        if (cardService.getCard(id).isEmpty())
            throw new CardNotFoundException(id);

        if ( !(cardService.getCard(id).get().getUser().getId().equals(getCurrentUserId())) )
            throw new ForbiddenOperationException("You are not allowed to see this card transactions.");

        List<TransactionDto> transactionDtos = transactionService.getAllTransactionsByCardId(id);

        if (transactionDtos.isEmpty())
            throw new EmptyListException("Transaction list is empty.");


        return transactionService.getAllTransactionsByCardId(id);
    }

    /**
     * Executes a transaction between two cards owned by the user.
     *
     * @param transactionDto the transaction details
     * @return response message indicating success or failure
     */
    @PostMapping("/cards/transaction")
    public ResponseEntity<String> addCardTransaction(@RequestBody TransactionDto transactionDto){
        if (cardService.getCard(transactionDto.getFromCardId()).isEmpty())
            throw new CardNotFoundException(transactionDto.getFromCardId());
        if (cardService.getCard(transactionDto.getToCardId()).isEmpty())
            throw new CardNotFoundException(transactionDto.getToCardId());

        if ( !(cardService.getCard(transactionDto.getFromCardId()).get().getUser().getId().equals(getCurrentUserId())) ||
                !(cardService.getCard(transactionDto.getToCardId()).get().getUser().getId().equals(getCurrentUserId()))) {
            throw new ForbiddenOperationException();
        }

        try {
            transactionService.executeTransaction(transactionDto);
        } catch (RuntimeException e) {
            throw new UnknownErrorException();
        }

        return ResponseEntity.ok("Translation completed successfully.");
    }

    /**
     * Withdraws money from the user's card if it does not exceed their monthly limit.
     *
     * @param id     the card ID
     * @param amount the amount to withdraw
     * @return response message indicating success or failure
     */
    @PostMapping("/cards/{id}/withdraw/{amount}")
    private ResponseEntity<String> withdrawMoney(@PathVariable Long id, @PathVariable BigDecimal amount){
        if (cardService.getCard(id).isEmpty())
            throw new CardNotFoundException(id);

        TransactionDto transactionDto;

        try {
            if (getCurrentUser().getMonthlyLimit() != null && (new BigDecimal(getCurrentUser().getMonthlyLimit()).compareTo(transactionService.getMonthlyWithdrawSum().add(amount)) > 0)) {
                transactionDto = new TransactionDto(
                        id,
                        null,
                        amount,
                        new Timestamp(System.currentTimeMillis())
                );

                transactionService.executeTransaction(transactionDto);
            } else if (getCurrentUser().getMonthlyLimit() == null) {
                transactionDto = new TransactionDto(
                        id,
                        null,
                        amount,
                        new Timestamp(System.currentTimeMillis())
                );

                transactionService.executeTransaction(transactionDto);
            } else {
                return ResponseEntity.ok("Limit on withdrawal of funds.");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new UnknownErrorException();
        }

        return ResponseEntity.ok("The withdrawal of funds has been successfully completed.");
    }

    /**
     * Retrieves the ID of the currently authenticated user.
     *
     * @return the user's ID
     */
    private Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    /**
     * Retrieves the currently authenticated user.
     *
     * @return the user
     */
    private User getCurrentUser() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
