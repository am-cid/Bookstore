package com.simple.Bookstore.Review;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        String userDisplayName,
        Long bookId,
        String bookTitle,
        LocalDateTime date,
        boolean edited,
        Double rating,
        String content
) {
}
