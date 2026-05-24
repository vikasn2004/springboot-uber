package com.uber.exceptions;

public class RatingAlreadyExistsException extends RuntimeException {
    public RatingAlreadyExistsException(String message) {
        super(message);
    }
}
