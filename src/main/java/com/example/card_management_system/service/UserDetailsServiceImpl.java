package com.example.card_management_system.service;

import com.example.card_management_system.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation of {@link UserDetailsService} used by Spring Security to retrieve user details
 * during the authentication process.
 * <p>
 * It loads a user from the database by their email address.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a new UserDetailsServiceImpl with the given user repository.
     *
     * @param userRepository the user repository used to find users by email
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by their email address.
     *
     * @param email the email address of the user to load
     * @return a {@link UserDetails} object representing the authenticated user
     * @throws UsernameNotFoundException if no user with the given email exists
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден."));
    }
}
