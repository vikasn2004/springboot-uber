package com.uber.DTO;

import lombok.Data;

@Data
public class GetAllDrivers {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
