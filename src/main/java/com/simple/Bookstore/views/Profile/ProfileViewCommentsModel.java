package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Comment.CommentViewResponseDTO;
import org.springframework.data.domain.Page;

public record ProfileViewCommentsModel(
        Page<CommentViewResponseDTO> profileComments
) {
}
