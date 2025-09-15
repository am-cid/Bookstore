package com.simple.Bookstore.Review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequestDTO(
        @NotNull(message = "Rating must not be null.")
        @DecimalMin(value = "1", message = "Rating must be at least 1 star.")
        @DecimalMax(value = "5", message = "Rating must not exceed 5 stars.")
        Integer rating,

        @Size(max = 100, message = "Stop yapping! 100 chars only!")
        String title,

        @Size(max = 2000, message = "Stop yapping! 2000 chars only!")
        String content
) {
    public static ReviewRequestDTO empty() {
        return new ReviewRequestDTO(null, null, null);
    }

    /**
     * @return whether the request is a blank request
     */
    public boolean isEmpty() {
        return rating == null;
    }
}
