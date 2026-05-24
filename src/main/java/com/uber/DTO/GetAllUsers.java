package com.uber.DTO;
import lombok.Data;


@Data
public class GetAllUsers {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
}
