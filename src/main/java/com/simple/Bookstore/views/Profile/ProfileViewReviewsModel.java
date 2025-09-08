package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Review.ReviewViewResponseDTO;
import org.springframework.data.domain.Page;

public record ProfileViewReviewsModel(
        Page<ReviewViewResponseDTO> profileReviews
) {
}
