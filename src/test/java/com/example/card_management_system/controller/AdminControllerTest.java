package com.example.card_management_system.controller;

import com.example.card_management_system.dto.CardDto;
import com.example.card_management_system.dto.LoginRequest;
import com.example.card_management_system.dto.UserDto;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
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
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    private LoginRequest loginRequest = new LoginRequest("admin@example.com", "adminpass");

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
    public void testGetAllCards_returnsCardsFromDatabase() throws Exception {
    mockMvc.perform(get("/api/admin/cards")
                    .header("Authorization", "Bearer " + jwtToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].number").exists())
            .andExpect(jsonPath("$[0].expiration_date").exists())
            .andExpect(jsonPath("$[0].status").exists())
            .andExpect(jsonPath("$[0].balance").exists())
            .andExpect(jsonPath("$[0].userId").exists());
    }

    @Test
    void testCreateCard_returnsCardDto() throws Exception {
        CardDto cardDto = new CardDto(
                "1212131314141515",
                        new Date(),
                        "ACTIVE",
                        new BigDecimal("0"),
                        1L
        );

        String cardJson = objectMapper.writeValueAsString(cardDto);

         mockMvc.perform(post("/api/admin/cards/create")
                         .header("Authorization", "Bearer " + jwtToken)
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(cardJson))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.number").exists())
                 .andExpect(jsonPath("$.expiration_date").exists())
                 .andExpect(jsonPath("$.status").exists())
                 .andExpect(jsonPath("$.balance").exists())
                 .andExpect(jsonPath("$.userId").exists());


    }

    @Test
    void testDeleteCard() throws Exception {
        Long id = cardRepository.findTopByOrderByIdDesc().getId();

        mockMvc.perform(delete("/api/admin/cards/" + id + "/delete")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        assertTrue(cardRepository.findById(id).isEmpty());
    }

    @Test
    void testGetCardTransactions_returnsTransactionsFromDataBase() throws Exception {
        mockMvc.perform(get("/api/admin/cards/1/transactions")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].fromCardId").exists())
                .andExpect(jsonPath("$[0].toCardId").exists())
                .andExpect(jsonPath("$[0].amount").exists())
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[1].fromCardId").exists())
                .andExpect(jsonPath("$[1].toCardId").exists())
                .andExpect(jsonPath("$[1].amount").exists())
                .andExpect(jsonPath("$[1].createdAt").exists());
    }

    @Test
    void testBlockCardAndActivateCard() throws Exception {
        mockMvc.perform(post("/api/admin/cards/1/block")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/admin/cards/1/activate")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUsers_returnAllUsersFromDataBase() throws Exception {

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].password").exists())
                .andExpect(jsonPath("$[0].first_Name").exists())
                .andExpect(jsonPath("$[0].last_Name").exists())
                .andExpect(jsonPath("$[0].role").exists());
    }

    @Test
    void testCreateUser_returnUserDto() throws Exception {
        UserDto userDto = new UserDto(
                "emailtest1212@exmple.com",
                "password123",
                "TestUser",
                "TestUser",
                "USER"
        );

        mockMvc.perform(post("/api/admin/users/create")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists())
                .andExpect(jsonPath("$.first_Name").exists())
                .andExpect(jsonPath("$.last_Name").exists())
                .andExpect(jsonPath("$.role").exists());


    }

    @Test
    void testDeleteUser() throws Exception {
        Long id = userRepository.findTopByOrderByIdDesc().getId();

        mockMvc.perform(delete("/api/admin/users/" + id + "/delete")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        assertTrue(userRepository.findById(id).isEmpty());
    }

    @Test
    void testSetUserLimit() throws Exception {
        mockMvc.perform(post("/api/admin/users/1/limit/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

}
