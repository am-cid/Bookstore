package com.simple.Bookstore.Review;

import com.simple.Bookstore.Auth.SecurityService;
import com.simple.Bookstore.Book.BookRepository;
import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final SecurityService securityService;

    @Override
    public List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId) {
        return reviewRepository
                .findAllReviewsByBookId(bookId)
                .stream()
                .map(this::reviewToResponseDTO)
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> findLatestNReviews(int n) {
        return reviewRepository
                .findTopNByOrderByIdDesc(n)
                .stream()
                .map(this::reviewToResponseDTO)
                .toList();
    }

    @Override
    public ReviewResponseDTO findReviewById(Long id) {
        return reviewRepository
                .findById(id)
                .map(this::reviewToResponseDTO)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    public ReviewResponseDTO createReview(Long bookId, ReviewRequestDTO request) {
        Review review = requestDtoToReview(bookId, request);
        Review savedReview = reviewRepository.save(review);
        return reviewToResponseDTO(savedReview);
    }

    @Override
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO request) {
        Review review = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        if (!review.getUser().getId().equals(securityService.getLoggedInUser().getId()))
            throw new UnauthorizedException("You are not authorized to edit this review");

        review.setContent(request.content());
        review.setRating(request.rating());
        review.setEdited(true);
        Review savedReview = reviewRepository.save(review);
        return reviewToResponseDTO(savedReview);
    }

    @Override
    public void deleteReview(Long id) {
        Review review = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        if (!review.getUser().getId().equals(securityService.getLoggedInUser().getId()))
            throw new UnauthorizedException("You are not authorized to delete this review");

        reviewRepository.deleteById(id);
    }

    // HELPERS
    private ReviewResponseDTO reviewToResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getUser().getId(),
                review.getUser().getDisplayName(),
                review.getRating(),
                review.getDate(),
                review.isEdited(),
                review.getContent()
        );
    }

    private Review requestDtoToReview(Long bookId, ReviewRequestDTO request) {
        Review review = new Review();
        review.setUser(securityService.getLoggedInUser());
        review.setBook(bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId)));
        review.setRating(request.rating());
        review.setContent(request.content());
        return review;
    }

}
