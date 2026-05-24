package com.uber.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(ex.getMessage());
        response.setStatus(409);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleException(MethodArgumentNotValidException ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage("validation error");
        response.setStatus(400);
        response.setTimestamp(LocalDateTime.now());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        response.setErrors(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }
    @ExceptionHandler(RatingAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRatingAlreadyExistsException(RatingAlreadyExistsException e){
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(e.getMessage());
        response.setStatus(400);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(ex.getMessage());
        response.setStatus(400);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    @ExceptionHandler(DriverUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleDriverUnavailable(DriverUnavailableException driverUnavailable) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(driverUnavailable.getMessage());
        response.setStatus(400);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    @ExceptionHandler(RideUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleRideUnavailable(RideUnavailableException driverUnavailable) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(driverUnavailable.getMessage());
        response.setStatus(400);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
  @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleDriverNotFound(DriverNotFoundException driverNotFound) {
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(driverNotFound.getMessage());
        response.setStatus(400);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }
  @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        ApiErrorResponse response = new ApiErrorResponse();
        response.setMessage(userNotFoundException.getMessage());
        response.setStatus(400);
        response.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

  }
}
