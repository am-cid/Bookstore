package com.simple.Bookstore.Comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDTO(
        @NotNull(message = "Comment must not be null.")
        @Size(max = 2000, message = "Stop yapping! 2000 chars only!")
        String content
) {
}
