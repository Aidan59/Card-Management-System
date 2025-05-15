package com.example.card_management_system.controller;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.dto.UserDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.service.CardService;
import com.example.card_management_system.service.TransactionService;
import com.example.card_management_system.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller provides administrative endpoints for managing users and cards,
 * including card creation, activation/blocking, transaction retrieval, and user limit settings.
 *
 * All endpoints are prefixed with "/api/admin".
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CardService cardService;
    private final UserService userService;
    private final TransactionService transactionService;

    /**
     * Constructs an AdminController with the given services.
     *
     * @param userService service for managing users
     * @param cardService service for managing cards
     * @param transactionService service for managing transactions
     */
    public AdminController(UserService userService, CardService cardService, TransactionService transactionService) {
        this.userService = userService;
        this.cardService = cardService;
        this.transactionService = transactionService;
    }

    /**
     * Retrieves all cards in the system.
     *
     * @return a list of CardDto objects
     */
    @GetMapping("/cards")
    public List<CardDto> getAllCards() {
        return cardService.getAllCards();
    }

    /**
     * Creates a new card for a user.
     *
     * @param cardDto data of the card to be created
     * @return the created CardDto with masked card number
     * @throws Exception if encryption fails
     */
    @PostMapping("/cards/create")
    public CardDto createCard(@RequestBody CardDto cardDto) throws Exception {
        return cardService.createCard(cardDto);
    }

    /**
     * Retrieves all transactions for a specific card.
     *
     * @param id the ID of the card
     * @return a list of TransactionDto objects
     */
    @GetMapping("/cards/{id}/transactions")
    public List<TransactionDto> getCardTransactions(@PathVariable Long id){
        return transactionService.getAllTransactionsByCardId(id);
    }

    /**
     * Blocks the card with the specified ID.
     *
     * @param id the ID of the card to block
     * @return the updated Card object with BLOCKED status
     */
    @PostMapping("/cards/{id}/block")
    public Card blockCard(@PathVariable Long id) {
        return cardService.blockCard(id);
    }

    /**
     * Activates the card with the specified ID.
     *
     * @param id the ID of the card to activate
     * @return the updated Card object with ACTIVE status
     */
    @PostMapping("/cards/{id}/activate")
    public Card activateCard(@PathVariable Long id) {
        return cardService.activateCard(id);
    }

    /**
     * Deletes the card with the specified ID.
     *
     * @param id the ID of the card to delete
     */
    @DeleteMapping("/cards/{id}/delete")
    private void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of UserDto objects
     */
    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Creates a new user.
     *
     * @param userDto the data of the user to create
     * @return the created UserDto
     */
    @PostMapping("/users/create")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    /**
     * Sets a monthly limit for the specified user.
     *
     * @param id the ID of the user
     * @param limit the monthly limit to set
     */
    @PostMapping("/users/{id}/limit/{limit}")
    public void setUserLimit(@PathVariable Long id, @PathVariable Long limit) {
        userService.setUserLimit(id, limit);
    }

    /**
     * Deletes the user with the specified ID.
     *
     * @param id the ID of the user to delete
     */
    @DeleteMapping("/users/{id}/delete")
    private void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}