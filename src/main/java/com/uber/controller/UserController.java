package com.uber.controller;

import com.uber.DTO.*;
import com.uber.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uber/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/ride/fare")
    public ResponseEntity<RideFareDTO> getRideFare( @RequestParam Double pickupLatitude,
                                                    @RequestParam Double pickupLongitude,
                                                    @RequestParam Double dropOffLatitude,
                                                    @RequestParam Double dropOffLongitude) {
        return ResponseEntity.ok(userService.getRideFare(
                pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude));
}
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/ride/request")
    public ResponseEntity<RideRequestResponseDTO> rideRequest(@RequestBody RideRequestDTO rideRequestDTO) {
        return ResponseEntity.ok(userService.rideRequest(rideRequestDTO));
    }
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/ride/cancel/{rideId}")
    public ResponseEntity<String> cancelRideRequest(@PathVariable Long rideId) {
        return ResponseEntity.ok(userService.cancelRide(rideId));
    }
    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/ride/history/{userId}")
    public ResponseEntity<List<AllRidesDTO>> getAllRides(@PathVariable Long userId) {
        return  ResponseEntity.ok(userService.getallRides(userId));
    }
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/rate/driver/{rideId}")
    public ResponseEntity<String> giveDriverRating(@PathVariable Long rideId, @RequestBody RatingDTO ratingDTO) {
        return ResponseEntity.ok(userService.giveDriverRating(rideId,ratingDTO));
    }
}
