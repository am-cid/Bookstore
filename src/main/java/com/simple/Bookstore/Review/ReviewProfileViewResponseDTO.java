package com.simple.Bookstore.Review;

import java.time.LocalDateTime;

public record ReviewProfileViewResponseDTO(
        Long id,
        String title,
        String content,
        Integer rating,
        LocalDateTime date,
        boolean edited,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        String bookFrontImage,
        String username,
        String userDisplayName,
        Integer pageNumber
) {
}
