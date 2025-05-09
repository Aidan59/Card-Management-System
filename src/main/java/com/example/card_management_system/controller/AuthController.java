package com.example.card_management_system.controller;

import com.example.card_management_system.dto.JwtAuthenticationResponse;
import com.example.card_management_system.dto.LoginRequest;
import com.example.card_management_system.security.JwtTokenProvider;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for user authentication.
 *
 * This controller handles login requests and issues JWT tokens upon successful authentication.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Constructs a new {@code AuthController} with the required dependencies.
     *
     * @param authenticationManager the authentication manager used to authenticate user credentials
     * @param jwtTokenProvider the JWT token provider used to generate tokens after successful authentication
     */
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticates the user and returns a JWT token.
     *
     * @param loginRequest the login request containing the user's email and password
     * @return a {@code ResponseEntity} containing a {@code JwtAuthenticationResponse} with the generated token
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
