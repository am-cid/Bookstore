package com.simple.Bookstore.Review;

import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId);

    List<ReviewProfileViewResponseDTO> findLatestNReviews(int n);

    Optional<ReviewProfileViewResponseDTO> findLatestReview();

    ReviewResponseDTO findReviewById(Long id);

    ReviewResponseDTO createReview(User user, Long bookId, ReviewRequestDTO request);

    ReviewResponseDTO updateReview(User user, Long id, ReviewRequestDTO request);

    void deleteReview(User user, Long id);

    Long countByBookId(Long bookId);

    Page<ReviewProfileViewResponseDTO> findAllReviewsByUser(User user, Pageable pageable);

    /**
     * @param bookId    id of book
     * @param profileId may be null if user is anonymous
     * @param pageable  pageable passed in through path params
     * @return paged reviews
     */
    Page<ReviewResponseDTO> findAllReviewsByBookIdAsPage(Long bookId, Long profileId, Pageable pageable);
}
