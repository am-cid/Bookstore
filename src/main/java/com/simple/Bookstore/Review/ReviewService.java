package com.simple.Bookstore.Review;

import com.simple.Bookstore.User.User;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId);

    List<ReviewResponseDTO> findLatestNReviews(int n);

    ReviewResponseDTO findReviewById(Long id);

    ReviewResponseDTO createReview(User user, Long bookId, ReviewRequestDTO request);

    ReviewResponseDTO updateReview(User user, Long id, ReviewRequestDTO request);

    void deleteReview(User user, Long id);
}
