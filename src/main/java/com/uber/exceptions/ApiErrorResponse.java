package com.uber.exceptions;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@JsonPropertyOrder({"status", "message", "timestamp","errors"})
public class ApiErrorResponse {
    private String message;
    private int status;
    private Map<String, String > errors;
    private LocalDateTime timestamp;
}
