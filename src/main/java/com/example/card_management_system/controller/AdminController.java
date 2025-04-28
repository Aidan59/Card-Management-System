package com.example.card_management_system.controller;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.dto.UserDto;
import com.example.card_management_system.model.Card;
import com.example.card_management_system.service.CardService;
import com.example.card_management_system.service.TransactionService;
import com.example.card_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private final CardService cardService;
    private final UserService userService;
    private final TransactionService transactionService;

    public AdminController(UserService userService, CardService cardService, TransactionService transactionService) {
        this.userService = userService;
        this.cardService = cardService;
        this.transactionService = transactionService;
    }

    @GetMapping("/cards")
    public List<CardDto> getAllCards() {
        return cardService.getAllCards();
    }

    @PostMapping("/cards/create")
    public CardDto createCard(@RequestBody CardDto cardDto) throws Exception {
        return cardService.createCard(cardDto);
    }

    @GetMapping("/cards/{id}/transactions")
    public List<TransactionDto> getCardTransactions(@PathVariable Long id){
        return transactionService.getAllTransactionsByCardId(id);
    }

    @PostMapping("/cards/{id}/block")
    public Card blockCard(@PathVariable Long id) {
        return cardService.blockCard(id);
    }

    @PostMapping("/cards/{id}/activate")
    public Card activateCard(@PathVariable Long id) {
        return cardService.activateCard(id);
    }

    @DeleteMapping("/cards/{id}/delete")
    private void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }



    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/users/create")
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PostMapping("/users/{id}/limit/{limit}")
    public void setUserLimit(@PathVariable Long id, @PathVariable Long limit) {
        userService.setUserLimit(id, limit);
    }

    @DeleteMapping("/users/{id}/delete")
    private void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
