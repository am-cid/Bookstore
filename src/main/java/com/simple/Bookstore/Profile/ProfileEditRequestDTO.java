package com.simple.Bookstore.Profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileEditRequestDTO(
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 3, max = 20, message = "Username must be 3 and 20 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]*$", message = "Username can only contain letters, numbers, dots, dashes, and underscores")
        String username,

        String newPassword,
        String confirmNewPassword,

        @Size(min = 0, max = 50, message = "Display name must at most 50 characters")
        String displayName,

        Boolean isPublic,

        @NotBlank(message = "Please input the current password to change these data")
        String oldPassword
) {
    public boolean isUninitializedForEditing() {
        return username == null && displayName == null;
    }
}
