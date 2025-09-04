package com.simple.Bookstore.Review;

import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId);

    List<ReviewResponseDTO> findLatestNReviews(int n);

    Optional<ReviewResponseDTO> findLatestReview();

    ReviewResponseDTO findReviewById(Long id);

    ReviewResponseDTO createReview(User user, Long bookId, ReviewRequestDTO request);

    ReviewResponseDTO updateReview(User user, Long id, ReviewRequestDTO request);

    void deleteReview(User user, Long id);

    Page<ReviewResponseDTO> findAllReviewsByUser(User user, Pageable pageable);
}
