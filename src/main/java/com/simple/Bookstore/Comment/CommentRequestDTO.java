package com.simple.Bookstore.Comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDTO(
        @NotNull(message = "Comment must not be empty.")
        @Size(max = 2000, message = "Stop yapping! 2000 chars only!")
        String content
) {
    public static CommentRequestDTO empty() {
        return new CommentRequestDTO(null);
    }

    public boolean isEmpty() {
        return content == null;
    }
}
