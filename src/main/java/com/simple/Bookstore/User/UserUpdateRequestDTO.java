package com.simple.Bookstore.User;

public record UserUpdateRequestDTO(
        String username,
        String newPassword
) {
}
