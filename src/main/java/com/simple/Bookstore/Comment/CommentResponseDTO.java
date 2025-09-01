package com.simple.Bookstore.Comment;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        String username,
        String userDisplayName,
        LocalDateTime date,
        boolean edited,
        String content
) {
}
