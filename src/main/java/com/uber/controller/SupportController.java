package com.uber.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/support")
public class SupportController {
    private final ChatClient chatClient;
    public SupportController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    @PostMapping("/chat")
    public SupportResponse chat(@RequestBody SupportRequest request){

        String systemPrompt = switch (request.role()) {
            case "driver" -> """
                You are a friendly support agent for a ride-hailing app. You help DRIVERS.
                Key facts:
                - View pending rides: GET /driver/rides/pending
                - Accept a ride: PUT /driver/rides/{rideId}/accept
                - Cancel accepted ride: PUT /driver/rides/{rideId}/cancel
                - Start a ride: PUT /driver/rides/{rideId}/start
                - End a ride: PUT /driver/rides/{rideId}/end
                - View ride history: GET /driver/rides/history
                - View earnings: GET /driver/earnings?days=N
                - Rate a rider: POST /driver/rides/{rideId}/rating
                - Fare = base ₹50 + ₹10 per km (Haversine GPS distance)
                - Ride states: REQUESTED → ACCEPTED → ONGOING → COMPLETED or CANCELLED
                - Once ONGOING, cancellation is not allowed
                Be concise, empathetic, and helpful.
                """;
            default -> """
                You are a friendly support agent for a ride-hailing app. You help RIDERS.
                Key facts:
                - Request a ride: POST /rides/request with pickup + drop coordinates
                - Fare estimate: GET /rides/fare
                - Cancel a ride (only before ONGOING): PUT /rides/{rideId}/cancel
                - View ride history: GET /rides/history/{userId}
                - Rate a completed ride (1-5): POST /rides/{rideId}/rating
                - Fare = base ₹50 + ₹10 per km (Haversine GPS distance)
                - Ride states: REQUESTED → ACCEPTED → ONGOING → COMPLETED or CANCELLED
                Be concise, empathetic, and helpful.
                """;
        };
        String reply = chatClient.prompt()
                .system(systemPrompt)
                .user(request.message())
                .call()
                .content();

        return new SupportResponse(reply);
    }
    public record SupportRequest(String role, String message) {}
    public record SupportResponse(String reply) {}
}
