package com.simple.Bookstore.Comment;

import java.time.LocalDateTime;

public record CommentReviewViewResponseDTO(
        Long id,
        String content,
        LocalDateTime date,
        boolean edited,
        String username,
        String userDisplayName
) {
}
