package com.simple.Bookstore.Review;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequestDTO(
        @NotNull(message = "Rating must not be null.")
        @DecimalMin(value = "1.0", message = "Rating must be at least 1.0.")
        @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0.")
        Double rating,

        @Size(max = 2000, message = "Stop yapping! 2000 chars only!")
        String content
) {
}
