package com.microservices.userservice.controller;

import com.microservices.userservice.dto.AuthResponse;
import com.microservices.userservice.dto.LoginRequest;
import com.microservices.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validates JWT token and returns user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            if (userService.validateToken(jwtToken)) {
                String username = userService.getUsernameFromToken(jwtToken);
                Long userId = userService.getUserIdFromToken(jwtToken);
                String role = userService.getRoleFromToken(jwtToken);
                
                AuthResponse response = new AuthResponse();
                response.setToken(jwtToken);
                response.setUserId(userId);
                response.setUsername(username);
                response.setRole(com.microservices.userservice.entity.UserRole.valueOf(role));
                
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Returns information about the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<AuthResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            
            if (userService.validateToken(jwtToken)) {
                String username = userService.getUsernameFromToken(jwtToken);
                Long userId = userService.getUserIdFromToken(jwtToken);
                String role = userService.getRoleFromToken(jwtToken);
                
                AuthResponse response = new AuthResponse();
                response.setToken(jwtToken);
                response.setUserId(userId);
                response.setUsername(username);
                response.setRole(com.microservices.userservice.entity.UserRole.valueOf(role));
                
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(401).build();
    }
} 