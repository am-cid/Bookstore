package com.simple.Bookstore.User;

import jakarta.validation.constraints.NotBlank;

public record UserDeleteRequestDTO(
        @NotBlank(message = "Must input password to delete account.")
        String password
) {
}
