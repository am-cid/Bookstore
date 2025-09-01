package com.simple.Bookstore.Review;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Override
    public List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId) {
        return reviewRepository
                .findAllReviewsByBookId(bookId)
                .stream()
                .map(ReviewMapper::reviewToResponseDTO)
                .toList();
    }

    @Override
    public List<ReviewResponseDTO> findLatestNReviews(int n) {
        return reviewRepository
                .findTopNByOrderByIdDesc(n)
                .stream()
                .map(ReviewMapper::reviewToResponseDTO)
                .toList();
    }

    @Override
    public ReviewResponseDTO findReviewById(Long id) {
        return reviewRepository
                .findById(id)
                .map(ReviewMapper::reviewToResponseDTO)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    public ReviewResponseDTO createReview(User user, Long bookId, ReviewRequestDTO request) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        Review review = ReviewMapper.requestDtoToReview(user, book, request);
        Review savedReview = reviewRepository.save(review);
        return ReviewMapper.reviewToResponseDTO(savedReview);
    }

    @Override
    public ReviewResponseDTO updateReview(User user, Long id, ReviewRequestDTO request) {
        Review review = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        if (!review.getProfile().getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("You are not authorized to edit this review");

        review.setContent(request.content());
        review.setRating(request.rating());
        review.setEdited(true);
        Review savedReview = reviewRepository.save(review);
        return ReviewMapper.reviewToResponseDTO(savedReview);
    }

    @Override
    public void deleteReview(User user, Long id) {
        Review review = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        if (!review.getProfile().getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("You are not authorized to delete this review");

        reviewRepository.deleteById(id);
    }

    // HELPERS
}
