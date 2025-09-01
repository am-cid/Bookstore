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

        @NotBlank(message = "Confirm Password cannot be empty")
        String confirmPassword,

        @Size(min = 0, max = 50, message = "Display name must at most 50 characters")
        String displayName,

        Boolean isPublic
) {
    /**
     * @return empty register request dto where all fields are null (isPublic is false)
     */
    public static RegisterRequestDTO empty() {
        return new RegisterRequestDTO(null, null, null, null, false);
    }

    /**
     * @return whether the request is a blank request
     */
    public boolean isEmpty() {
        return (username == null || username.isEmpty())
                && (password == null || password.isEmpty())
                && (confirmPassword == null || confirmPassword.isEmpty())
                && (displayName == null || displayName.isEmpty());
    }
}
