package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Comment.CommentResponseDTO;
import org.springframework.data.domain.Page;

public record ProfileViewCommentsModel(
        Page<CommentResponseDTO> profileComments
) {
}
