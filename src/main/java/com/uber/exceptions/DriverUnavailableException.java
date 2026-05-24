package com.uber.exceptions;

public class DriverUnavailableException extends RuntimeException {
    public DriverUnavailableException(String message) {
        super(message);
    }
}
