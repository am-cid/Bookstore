package com.simple.Bookstore.views.Review;

import com.simple.Bookstore.Comment.CommentReviewViewResponseDTO;
import com.simple.Bookstore.Review.ReviewViewResponseDTO;
import org.springframework.data.domain.Page;

public record ReviewViewModel(
        Long bookId,
        ReviewViewResponseDTO review,
        Page<CommentReviewViewResponseDTO> comments
) {
}
