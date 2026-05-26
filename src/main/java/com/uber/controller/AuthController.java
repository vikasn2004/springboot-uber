package com.uber.controller;

import com.uber.DTO.DriverRegisterDTO;
import com.uber.DTO.LoginDTO;
import com.uber.DTO.UserRegisterDTO;
import com.uber.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/uber")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.userRegister(userRegisterDTO));
    }
    @PostMapping("/register/driver")
    public ResponseEntity<String> registerDriver(@Valid @RequestBody DriverRegisterDTO driverRegisterDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.driverRegister(driverRegisterDTO));
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(authService.login(loginDTO));
    }

}
