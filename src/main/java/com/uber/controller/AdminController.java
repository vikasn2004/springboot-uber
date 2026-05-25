package com.uber.controller;

import com.uber.DTO.EarningsDTO;
import com.uber.DTO.GetAllDrivers;
import com.uber.DTO.GetAllUsers;
import com.uber.DTO.GetDriverDetails;
import com.uber.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/uber/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<GetAllUsers>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/drivers")
    public ResponseEntity<List<GetAllDrivers>> getAllDrivers() {
        return ResponseEntity.ok(adminService.getAllDrivers());
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("driver/{driverId}")
    public ResponseEntity<GetDriverDetails> getDriverDetails(@PathVariable Long driverId){
        return ResponseEntity.ok(adminService.getDriverDetails(driverId));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("driver/earnings/{driverId}/{period}")
    public ResponseEntity<EarningsDTO> getEarnings(@PathVariable Long driverId, @PathVariable Long period) {
        return ResponseEntity.ok(adminService.getEarnings(driverId,period));
    }
}
