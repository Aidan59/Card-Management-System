package com.example.card_management_system.service;

import com.example.card_management_system.dto.UserDto;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing user-related operations such as retrieving,
 * creating, updating limits, and deleting users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users from the database and converts them to UserDto objects.
     *
     * @return a list of UserDto representing all users
     */
    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDto(
                user.getEmail(),
                user.getPassword(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getRole().toString()
        )).collect(Collectors.toList());
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user
     * @return the User object if found, otherwise null
     */
    public User getUserById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Creates a new user in the database with encoded password and role.
     *
     * @param userDto the user data to be saved
     * @return the same UserDto object that was passed in
     */
    public UserDto createUser(UserDto userDto){
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirst_name(userDto.getLast_Name());
        user.setLast_name(userDto.getLast_Name());
        user.setRole(User.Role.valueOf(userDto.getRole()));

        userRepository.save(user);

        return userDto;
    }

    /**
     * Sets a monthly spending limit for the user.
     *
     * @param id    the ID of the user
     * @param limit the monthly limit to be set
     */
    public void setUserLimit(Long id, Long limit){
        User user = userRepository.findById(id).get();
        user.setMonthlyLimit(limit);

        userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
