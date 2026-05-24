package com.uber.controller;

import com.uber.DTO.*;
import com.uber.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/uber/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @PreAuthorize("hasAnyRole('DRIVER')")
    @GetMapping("/pending/rides")
    public ResponseEntity<List<AllPendingRidesDTO>> getAllRides() {
        return ResponseEntity.ok(driverService.getAllPendingRides());
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/ride/accept/{rideId}")
    public ResponseEntity<AcceptRideResponseDTO> acceptRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(driverService.rideAccept(rideId));
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/ride/cancel/{rideId}")
    public ResponseEntity<String> cancelRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(driverService.cancelRide(rideId));
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/ride/started/{rideId}")
    public ResponseEntity<String> startRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(driverService.startedRide(rideId));
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/ride/end/{rideId}")
    public ResponseEntity<EndRideDTO> endRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(driverService.endRide(rideId));
    }

    @PreAuthorize("hasAnyRole('DRIVER')")
    @GetMapping("/ride/history")
    public ResponseEntity<List<AllRidesDTO>> getAllRidesHistory() {
        return ResponseEntity.ok(driverService.getAllRides());
    }
    @PreAuthorize("hasAnyRole('DRIVER')")
    @GetMapping("/getEarnings/{days}")
    public ResponseEntity<EarningsDTO> getEarnings(@PathVariable Long days) {
        return ResponseEntity.ok(driverService.getEarnings(days));
    }
    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/rate/rider/{rideId}")
    public ResponseEntity<String> giveRatingForRider(@PathVariable Long rideId,
                                                     @RequestBody RatingDTO ratingDTO) {
        return ResponseEntity.ok(driverService.giveRatingForRider(rideId,ratingDTO));
    }


    }

