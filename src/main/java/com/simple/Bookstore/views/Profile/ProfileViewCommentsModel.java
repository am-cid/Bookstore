package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Comment.CommentProfileViewResponseDTO;
import org.springframework.data.domain.Page;

public record ProfileViewCommentsModel(
        Page<CommentProfileViewResponseDTO> profileComments
) {
}
