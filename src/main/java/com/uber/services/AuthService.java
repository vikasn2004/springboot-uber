package com.uber.services;


import com.uber.DTO.DriverRegisterDTO;
import com.uber.DTO.LoginDTO;
import com.uber.DTO.UserRegisterDTO;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

public interface AuthService {
    public String userRegister(UserRegisterDTO userRegisterDTO);
    public String driverRegister(DriverRegisterDTO driverRegisterDTO);
    public String login(@Valid LoginDTO loginDTO);
}
