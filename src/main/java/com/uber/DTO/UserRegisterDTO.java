package com.uber.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisterDTO {
    @NotBlank(message = "name field cannopt be empty")
     String name;

    @NotBlank(message = "password must be provided")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be 8+ chars, include uppercase, lowercase, number, and special character"
    )
    String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "must be valid format ")
     String email;

    @NotBlank(message = "phone number must be provided")
    @Pattern(regexp = "^[0-9]{10}$",message = "enter 10 digit valid phone number")
     String phoneNumber;

}
