package com.example.card_management_system.dto;

import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String password;
    private String first_Name;
    private String last_Name;
    private String role;

    public UserDto(String email, String password, String first_Name, String last_Name, String role) {
        this.email = email;
        this.password = password;
        this.first_Name = first_Name;
        this.last_Name = last_Name;
        this.role = role;
    }
}
