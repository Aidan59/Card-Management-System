package com.example.card_management_system.service;

import com.example.card_management_system.dto.UserDto;
import com.example.card_management_system.model.User;
import com.example.card_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers_returnUserDtos() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password123");
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setRole(User.Role.USER);

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("user@example.com", result.get(0).getEmail());
        assertEquals("John", result.get(0).getFirst_Name());
        assertEquals("Doe", result.get(0).getLast_Name());
        assertEquals("USER", result.get(0).getRole());
    }

    @Test
    void testGetUserById_whenUserExists_returnUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetUserById_whenUserDoesNotExist_shouldReturnNull() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.getUserById(99L);

        assertNull(result);
    }

    @Test
    void testCreateUser_shouldSaveUserAndReturnDto() {
        UserDto userDto = new UserDto(
                "new@example.com", "password123", "John", "Doe", "USER"
        );

        when(passwordEncoder.encode("password123")).thenReturn("hashed123");

        User savedUser = new User();
        savedUser.setEmail(userDto.getEmail());
        savedUser.setPassword("hashed123");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(userDto);

        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void setUserLimit_shouldUpdateLimit() {
        User user = new User();
        user.setId(1L);
        user.setMonthlyLimit(0L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.setUserLimit(1L, 500L);

        assertEquals(500L, user.getMonthlyLimit());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteUser_shouldCallRepositoryDeleteById() {
        userService.deleteUser(10L);
        verify(userRepository, times(1)).deleteById(10L);
    }
}
