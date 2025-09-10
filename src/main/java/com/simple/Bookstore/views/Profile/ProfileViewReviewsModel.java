package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Review.ReviewProfileViewResponseDTO;
import org.springframework.data.domain.Page;

public record ProfileViewReviewsModel(
        Page<ReviewProfileViewResponseDTO> profileReviews
) {
}
