package com.simple.Bookstore.Comment;

import java.time.LocalDateTime;

public record CommentProfileViewResponseDTO(
        Long id,
        String content,
        LocalDateTime date,
        boolean edited,
        Long reviewId,
        String reviewTitle,
        String reviewerUsername,
        String reviewerDisplayName,
        Long bookId,
        String username,
        String userDisplayName,
        Integer pageNumber
) {
}
