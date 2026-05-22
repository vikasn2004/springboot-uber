package com.uber.controller;

import com.uber.DTO.RideFareDTO;
import com.uber.DTO.RideRequestDTO;
import com.uber.DTO.RideRequestResponseDTO;
import com.uber.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uber")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/ride/fare")
    public ResponseEntity<RideFareDTO> getRideFare( @RequestParam Double pickupLatitude,
                                                    @RequestParam Double pickupLongitude,
                                                    @RequestParam Double dropOffLatitude,
                                                    @RequestParam Double dropOffLongitude) {
        return ResponseEntity.ok(userService.getRideFare(
                pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude));
}

    @PostMapping("/ride/request")
    public ResponseEntity<RideRequestResponseDTO> rideRequest(@RequestBody RideRequestDTO rideRequestDTO) {
        return ResponseEntity.ok(userService.rideRequest(rideRequestDTO));
    }
}
