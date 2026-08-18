package com.ramas.dto;

import com.ramas.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
            @NotBlank String fullName,
            String affiliation,
            UserRole role
    ) {}

    public record AuthResponse(
            String token,
            String tokenType,
            long expiresIn,
            UserDto user
    ) {
        public static AuthResponse of(String token, long expiresIn, UserDto user) {
            return new AuthResponse(token, "Bearer", expiresIn, user);
        }
    }

    public record UserDto(
            UUID id,
            String email,
            String fullName,
            String affiliation,
            UserRole role,
            boolean enabled
    ) {}
}
