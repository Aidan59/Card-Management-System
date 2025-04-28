package com.example.card_management_system.service;

import com.example.card_management_system.dto.UserDto;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.CardRepository;
import com.example.card_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;



    public UserService(UserRepository userRepository, CardRepository cardRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;

    }

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

    public void setUserLimit(Long id, Long limit){
        User user = userRepository.findById(id).get();
        user.setMonthlyLimit(limit);

        userRepository.save(user);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
