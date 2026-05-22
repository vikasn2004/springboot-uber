package com.uber.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
     String email;
    @NotBlank(message = "password must be provided")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be 8+ chars, include uppercase, lowercase, number, and special character"
    )
     String password;
}
