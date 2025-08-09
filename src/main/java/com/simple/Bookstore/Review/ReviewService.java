package com.simple.Bookstore.Review;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId);

    List<ReviewResponseDTO> findLatestNReviews(int n);

    ReviewResponseDTO findReviewById(Long id);

    ReviewResponseDTO createReview(Long bookId, ReviewRequestDTO request);

    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO request);

    void deleteReview(Long id);
}
