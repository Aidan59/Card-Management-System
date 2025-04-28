package com.example.card_management_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/test")
    public ResponseEntity<String> test(){

        return ResponseEntity.ok("Hello Admin");
    }
}
