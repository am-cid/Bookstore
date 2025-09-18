package com.simple.Bookstore.Review;

import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewResponseDTO findReviewById(Long id) throws ReviewNotFoundException;

    List<ReviewResponseDTO> findAllPublicOrOwnedReviewsByBookId(Long bookId);

    Optional<ReviewProfileViewResponseDTO> findLatestReview();

    ReviewResponseDTO createReview(User user, Long bookId, ReviewRequestDTO request)
            throws IllegalStateException, BookNotFoundException;

    ReviewResponseDTO updateReview(User user, Long id, ReviewRequestDTO request);

    void deleteReview(User user, Long id);

    boolean isAlreadyReviewedByUser(Long bookId, User user);

    Long countByBookId(Long bookId);

    List<ReviewProfileViewResponseDTO> findLatestNReviews(int n);

    Page<ReviewProfileViewResponseDTO> findAllReviewsByUser(User user, Pageable pageable);

    /**
     * @param bookId    id of book
     * @param profileId may be null if user is anonymous
     * @param pageable  pageable passed in through path params
     * @return paged reviews
     */
    Page<ReviewBookViewResponseDTO> findAllPublicOrOwnedReviewsByBookIdAsPage(Long bookId, Long profileId, Pageable pageable);
}
