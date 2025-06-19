package com.example.card_management_system.controller;


import com.example.card_management_system.dto.LoginRequest;
import com.example.card_management_system.dto.TransactionDto;
import com.example.card_management_system.repository.BlockCardRequestRepository;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import com.example.card_management_system.service.BlockCardRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private BlockCardRequestService blockCardRequestService;

    private LoginRequest loginRequest = new LoginRequest("user1@example.com", "password123");

    private String obtainJwtToken() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn().getResponse().getContentAsString();

        String token = JsonPath.read(response, "$.accessToken");

        return token;
    }

    @BeforeEach
    void setup() throws Exception {
        if (jwtToken == null) {
            jwtToken = obtainJwtToken();
        }
    }

    @Test
    void testGetAllUserCards_returnCardsFromDataBase() throws Exception {
        mockMvc.perform(get("/api/user/cards")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].number").exists())
                .andExpect(jsonPath("$[0].expiration_date").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].balance").exists())
                .andExpect(jsonPath("$[0].userId").exists());

    }

    @Test
    void testBlockCardRequest() throws Exception {
        Long cardId = 2L;

        mockMvc.perform(post("/api/user/cards/" + cardId + "/block_request")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/user/cards/" + cardId + "/block_request")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/user/cards/999/block_request")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

        blockCardRequestService.deleteCardBlockRequest(cardId);
    }

    @Test
    void testGetAllCardTransactions_returnCardTransactionsFromDataBase() throws Exception {
        mockMvc.perform(get("/api/user/cards/2/transactions")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].fromCardId").exists())
                .andExpect(jsonPath("$[0].toCardId").exists())
                .andExpect(jsonPath("$[0].amount").exists())
                .andExpect(jsonPath("$[0].createdAt").exists());

        mockMvc.perform(get("/api/user/cards/999/transactions")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/user/cards/3/transactions")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testTransaction() throws Exception {
        TransactionDto transactionDto1 = new TransactionDto(
                2L,
                5L,
                new BigDecimal("10"),
                new Timestamp(System.currentTimeMillis())
        );

        TransactionDto transactionDto2 = new TransactionDto(
                5L,
                2L,
                new BigDecimal("10"),
                new Timestamp(System.currentTimeMillis())
        );

        TransactionDto transactionDto3 = new TransactionDto(
                5L,
                1L,
                new BigDecimal("10"),
                new Timestamp(System.currentTimeMillis())
        );

        TransactionDto transactionDto4 = new TransactionDto(
                999L,
                888L,
                new BigDecimal("10"),
                new Timestamp(System.currentTimeMillis())
        );

        mockMvc.perform(post("/api/user/cards/transaction")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDto1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/user/cards/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/user/cards/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto3)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/user/cards/transaction")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto4)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testWithdrawMoney() throws Exception {
        mockMvc.perform(post("/api/user/cards/2/withdraw/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

}
