package com.microservices.userservice.service;

import com.microservices.userservice.dto.AuthResponse;
import com.microservices.userservice.dto.CreateUserRequest;
import com.microservices.userservice.dto.LoginRequest;
import com.microservices.userservice.dto.UserDto;
import com.microservices.userservice.entity.User;
import com.microservices.userservice.entity.UserRole;
import com.microservices.userservice.exception.AuthenticationException;
import com.microservices.userservice.exception.UserAlreadyExistsException;
import com.microservices.userservice.exception.UserNotFoundException;
import com.microservices.userservice.repository.UserRepository;
import com.microservices.userservice.util.JwtUtil;
import com.microservices.userservice.util.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public UserDto createUser(CreateUserRequest request) {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        
        // Publish user created event
        kafkaProducerService.publishUserCreated(savedUser.getUsername(), savedUser.getId());
        
        return convertToDto(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by username or email
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), 
                request.getUsernameOrEmail()
        );

        if (userOptional.isEmpty()) {
            throw new AuthenticationException();
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException();
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return convertToDto(user);
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("username", username));
        return convertToDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email", email));
        return convertToDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Check if new username/email already exists (excluding current user)
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("username", request.getUsername());
        }

        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        // Update user
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        
        // Publish user updated event
        kafkaProducerService.publishUserUpdated(savedUser.getUsername(), savedUser.getId());
        
        return convertToDto(savedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
        // Publish user deleted event
        kafkaProducerService.publishUserDeleted(user.getUsername(), user.getId());
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }

    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    public String getRoleFromToken(String token) {
        return jwtUtil.getRoleFromToken(token);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
} 