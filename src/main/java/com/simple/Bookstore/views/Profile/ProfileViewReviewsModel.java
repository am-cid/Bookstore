package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Review.ReviewResponseDTO;
import org.springframework.data.domain.Page;

public record ProfileViewReviewsModel(
        Page<ReviewResponseDTO> profileReviews
) {
}
