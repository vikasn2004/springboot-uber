package com.uber.services;

import com.uber.DTO.GetAllDrivers;
import com.uber.DTO.GetAllUsers;
import com.uber.entity.Driver;
import com.uber.entity.User;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface AdminService {
    List<GetAllUsers> getAllUsers();

     List<GetAllDrivers> getAllDrivers();
}
