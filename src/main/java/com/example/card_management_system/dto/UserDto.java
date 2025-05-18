package com.example.card_management_system.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_Name() {
        return first_Name;
    }

    public void setFirst_Name(String first_Name) {
        this.first_Name = first_Name;
    }

    public String getLast_Name() {
        return last_Name;
    }

    public void setLast_Name(String last_Name) {
        this.last_Name = last_Name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
