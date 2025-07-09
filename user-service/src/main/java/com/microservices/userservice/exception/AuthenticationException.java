package com.microservices.userservice.exception;

public class AuthenticationException extends UserServiceException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException() {
        super("Invalid username/email or password");
    }
} 