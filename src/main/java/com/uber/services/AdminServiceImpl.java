package com.uber.services;

import com.uber.DTO.GetAllDrivers;
import com.uber.DTO.GetAllUsers;
import com.uber.entity.Driver;
import com.uber.entity.User;
import com.uber.repository.DriverRepo;
import com.uber.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepo userRepo;
    private final DriverRepo driverRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<GetAllUsers> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(u -> modelMapper.map(u, GetAllUsers.class))
                .collect(Collectors.toList());
    }

    @Override
    public  List<GetAllDrivers> getAllDrivers() {
        return driverRepo.findAll()
                .stream().map(d-> modelMapper.map(d, GetAllDrivers.class)).collect(Collectors.toList());
    }
}
