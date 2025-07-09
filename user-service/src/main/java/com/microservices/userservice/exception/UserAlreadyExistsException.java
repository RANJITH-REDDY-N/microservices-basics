package com.microservices.userservice.exception;

public class UserAlreadyExistsException extends UserServiceException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
} 