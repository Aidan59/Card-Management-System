package com.example.card_management_system.controller;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.model.BlockCardRequest;
import com.example.card_management_system.model.User;
import com.example.card_management_system.service.BlockCardRequestService;
import com.example.card_management_system.service.CardService;
import com.example.card_management_system.service.TransactionService;
import com.example.card_management_system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/user/")
public class UserController {

    private final UserService userService;
    private final CardService cardService;
    private final BlockCardRequestService blockCardRequestService;
    private final TransactionService transactionService;
    private final UserDetailsService userDetailsService;

    public UserController(UserService userService, CardService cardService, BlockCardRequestService blockCardRequestService, TransactionService transactionService, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.cardService = cardService;
        this.blockCardRequestService = blockCardRequestService;
        this.transactionService = transactionService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/cards")
    public List<CardDto> getAllUserCards(){
        return cardService.getAllUserCards(getCurrentUserId());
    }

    @PostMapping("/cards/{id}/block_request")
    public ResponseEntity<String> getBlockCardRequest(@PathVariable Long id){
        blockCardRequestService.makeCardBlockRequest(getCurrentUserId(), id);

        return ResponseEntity.ok("Заявка на блокировку карты оставлена.");
    }

    @GetMapping("/cards/{id}/transactions")
    public List<TransactionDto> getAllCardTransactions(@PathVariable Long id){

        if ( !(cardService.getCard(id).getUser().getId().equals(getCurrentUserId())) ) {
            return null;
        }

        return transactionService.getAllTransactionsByCardId(id);
    }

    @PostMapping("/cards/transaction")
    public ResponseEntity<String> addCardTransaction(@RequestBody TransactionDto transactionDto){


        if ( !(cardService.getCard(transactionDto.getFromCardId()).getUser().getId().equals(getCurrentUserId())) ||
                !(cardService.getCard(transactionDto.getToCardId()).getUser().getId().equals(getCurrentUserId()))) {
            return ResponseEntity.ok("Перевод должен осуществлятся только между своими картами.");
        }

        try {
            transactionService.executeTransaction(transactionDto);
        } catch (RuntimeException e) {
            return ResponseEntity.ok("Перевод не был доставлен.");
        }

        return ResponseEntity.ok("Перевод успешно совершён.");
    }

    @PostMapping("/cards/{id}/withdraw/{amount}")
    private ResponseEntity<String> withdrawMoney(@PathVariable Long id, @PathVariable BigDecimal amount){
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
                return ResponseEntity.ok("Лимит списания средств.");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.ok("Вывод средств не был совершён.");
        }

        return ResponseEntity.ok("Вывод средств успешно совершён.");
    }

    private Long getCurrentUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    private User getCurrentUser() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
