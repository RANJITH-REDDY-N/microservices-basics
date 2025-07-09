package com.microservices.userservice.service;

import com.microservices.userservice.dto.CreateUserRequest;
import com.microservices.userservice.dto.LoginRequest;
import com.microservices.userservice.entity.User;
import com.microservices.userservice.entity.UserRole;
import com.microservices.userservice.exception.AuthenticationException;
import com.microservices.userservice.exception.UserAlreadyExistsException;
import com.microservices.userservice.exception.UserNotFoundException;
import com.microservices.userservice.repository.UserRepository;
import com.microservices.userservice.util.JwtUtil;
import com.microservices.userservice.util.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for UserService.
 * Uses Spring Boot test context to avoid Mockito/Java 23 compatibility issues.
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(userRepository, jwtUtil, kafkaProducerService, passwordEncoder);
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUserSuccess() {
        CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com", "password123", UserRole.USER);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
        User savedUser = new User("testuser", "test@example.com", "$2a$10$hashed", UserRole.USER);
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        var dto = userService.createUser(request);
        assertEquals("testuser", dto.getUsername());
        assertEquals("test@example.com", dto.getEmail());
        verify(kafkaProducerService).publishUserCreated("testuser", 1L);
    }

    @Test
    @DisplayName("Should throw if username exists")
    void testCreateUserUsernameExists() {
        CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com", "password123", UserRole.USER);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("Should throw if email exists")
    void testCreateUserEmailExists() {
        CreateUserRequest request = new CreateUserRequest("testuser", "test@example.com", "password123", UserRole.USER);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("Should get user by ID")
    void testGetUserById() {
        User user = new User("testuser", "test@example.com", "hashed", UserRole.USER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        var dto = userService.getUserById(1L);
        assertEquals("testuser", dto.getUsername());
    }

    @Test
    @DisplayName("Should throw if user not found by ID")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    @DisplayName("Should login successfully with username")
    void testLoginSuccessWithUsername() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        User user = new User("testuser", "test@example.com", "$2a$10$hashed", UserRole.USER);
        user.setId(1L);
        when(userRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "$2a$10$hashed")).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyLong(), anyString())).thenReturn("token");

        var response = userService.login(request);
        assertEquals("token", response.getToken());
        assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("Should throw on login with unknown user")
    void testLoginUnknownUser() {
        LoginRequest request = new LoginRequest("unknown", "password123");
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> userService.login(request));
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }
    }
} 