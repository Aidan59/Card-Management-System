package com.example.card_management_system.controller;

import com.example.card_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class TestController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/test")
    @ResponseBody
    public String test() {

        return "";
    }
}
