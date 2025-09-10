package com.simple.Bookstore.Review;

import com.simple.Bookstore.Comment.CommentReviewViewResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponseDTO(
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
        List<CommentReviewViewResponseDTO> comments
) {
}
