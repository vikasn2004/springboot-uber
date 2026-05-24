package com.uber.controller;

import com.uber.DTO.GetAllDrivers;
import com.uber.DTO.GetAllUsers;
import com.uber.entity.Driver;
import com.uber.entity.User;
import com.uber.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
}
