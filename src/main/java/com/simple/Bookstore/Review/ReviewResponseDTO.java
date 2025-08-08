package com.simple.Bookstore.Review;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        Long userId,
        String displayName,
        Double rating,
        LocalDateTime date,
        String content
) {
}
