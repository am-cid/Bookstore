package com.simple.Bookstore.User;

import com.simple.Bookstore.utils.FormRequest;
import jakarta.validation.constraints.NotBlank;

public record UserDeleteRequestDTO(
        @NotBlank(message = "Must input password to delete account.")
        String password
) implements FormRequest {
    /**
     * @return with initialized empty string password
     */
    public static UserDeleteRequestDTO empty() {
        return new UserDeleteRequestDTO("");
    }

    @Override
    public boolean isUninitialized() {
        return password == null;
    }

}
