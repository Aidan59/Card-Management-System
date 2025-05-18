package com.example.card_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class JwtAuthenticationResponse {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
