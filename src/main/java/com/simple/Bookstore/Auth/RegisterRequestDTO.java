package com.simple.Bookstore.Auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 3, max = 20, message = "Username must be 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]*$", message = "Username can only contain letters, numbers, dots, dashes, and underscores")
        String username,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,30}$",
                message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
        String password,

        @Size(min = 1, max = 50, message = "Display name must be between 1 and 50 characters")
        String displayName,

        @NotBlank(message = "Profile must either be public or private")
        boolean isPublic
) {
}
