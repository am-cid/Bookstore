package com.simple.Bookstore.Comment;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        Long userId,
        String displayName,
        LocalDateTime date,
        boolean edited,
        String content
) {
}
