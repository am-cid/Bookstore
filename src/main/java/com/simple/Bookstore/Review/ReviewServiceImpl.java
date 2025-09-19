package com.simple.Bookstore.Review;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.config.constants.PagingConstants;
import com.simple.Bookstore.utils.ReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Override
    public ReviewResponseDTO findReviewById(Long id) throws ReviewNotFoundException {
        return reviewRepository
                .findById(id)
                .map(ReviewMapper::reviewToResponseDTO)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    public List<ReviewResponseDTO> findAllPublicOrOwnedReviewsByBookId(Long bookId) {
        return reviewRepository
                .findAllPublicOrOwnedReviewsByBookId(bookId)
                .stream()
                .map(ReviewMapper::reviewToResponseDTO)
                .toList();
    }

    @Override
    public Optional<ReviewProfileViewResponseDTO> findLatestReview(User user) {
        List<ReviewProfileViewResponseDTO> latestNReviews = findLatestNReviews(1, user);
        if (latestNReviews.isEmpty())
            return Optional.empty();
        return Optional.of(latestNReviews.getFirst());
    }

    @Override
    public List<ReviewProfileViewResponseDTO> findLatestNReviews(int n, User user) {
        return reviewRepository
                .findTopNByOrderByIdDesc(
                        n,
                        user != null ? user.getProfile().getId() : null,
                        PagingConstants.DEFAULT_PAGE_SIZE
                )
                .stream()
                .map(ReviewMapper::viewProjectionToViewResponseDTO)
                .toList();
    }

    @Override
    public ReviewResponseDTO createReview(
            User user, Long bookId,
            ReviewRequestDTO request
    ) throws IllegalStateException {
        if (isAlreadyReviewedByUser(bookId, user))
            throw new IllegalStateException("This book is already reviewed by user");

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

    @Override
    public Page<ReviewProfileViewResponseDTO> findAllReviewsByUser(User user, Pageable pageable) {
        if (user == null)
            return Page.empty(pageable);
        return reviewRepository
                .findAllReviewsByProfileId(user.getProfile().getId(), pageable.getPageSize(), pageable)
                .map(ReviewMapper::viewProjectionToViewResponseDTO);
    }

    @Override
    public Page<ReviewBookViewResponseDTO> findAllPublicOrOwnedReviewsByBookIdAsPage(Long bookId, Long profileId, Pageable pageable) {
        return reviewRepository
                .findAllPublicOrOwnedReviewsByBookId(bookId, profileId, pageable)
                .map(ReviewMapper::bookViewProjectionToBookViewResponseDTO);
    }

    @Override
    public ReviewViewResponseDTO findReviewViewById(Long id) throws ReviewNotFoundException {
        return reviewRepository
                .findReviewById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    @Override
    public Long countByBookId(Long bookId) {
        return reviewRepository.countByBookId(bookId);
    }

    @Override
    public boolean isAlreadyReviewedByUser(Long bookId, User user) {
        List<Review> result = reviewRepository.findByBookIdAndProfileId(bookId, user == null ? null : user.getProfile().getId());
        return result != null && !result.isEmpty();
    }
}
