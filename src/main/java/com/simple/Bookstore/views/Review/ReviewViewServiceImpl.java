package com.simple.Bookstore.views.Review;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Comment.CommentReviewViewResponseDTO;
import com.simple.Bookstore.Comment.CommentService;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Review.ReviewViewResponseDTO;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewViewServiceImpl implements ReviewViewService {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final CommentService commentService;

    @Override
    public Result<ReviewViewModel, String> validatePageAccess(
            User user,
            Long bookId,
            Long reviewId,
            Pageable pageable
    ) throws ReviewNotFoundException {
        ReviewViewResponseDTO review = reviewService.findReviewViewById(reviewId);
        Result<Void, String> redirectResult = validateAccess(user, bookId, reviewId, review);
        if (redirectResult.isErr())
            return new Result.Err<>(redirectResult.unwrapErr());
        Page<CommentReviewViewResponseDTO> comments = commentService.findAllPublicOrOwnedCommentsByReviewIdAsPage(reviewId, user, pageable);
        return new Result.Ok<>(new ReviewViewModel(
                bookId,
                review,
                comments
        ));
    }

    @Override
    public Result<ReviewViewModel, String> validateCommentCreationAccess(User user, Long bookId, Long reviewId, Pageable pageable) {
        ReviewViewResponseDTO review = reviewService.findReviewViewById(reviewId);
        Result<Void, String> redirectResult = validatePostAccess(user, bookId, reviewId, review);
        if (redirectResult.isErr())
            return new Result.Err<>(redirectResult.unwrapErr());
        Page<CommentReviewViewResponseDTO> comments = commentService.findAllPublicOrOwnedCommentsByReviewIdAsPage(reviewId, user, pageable);
        return new Result.Ok<>(new ReviewViewModel(
                bookId,
                review,
                comments
        ));
    }

    /**
     * validates whether user can access a review thread page.
     *
     * @param user     current user
     * @param bookId   book id
     * @param reviewId review id
     * @param review   review object that contains owner's username to validate ownership
     * @return A {@code Result.Ok} containing null if the user can access the review thread page.
     * Returns a {@code Result.Err} with a redirect URL if the review thread page:
     * <ul>
     *     <li>made by a private profile</li>
     *     <li>is private but not the owner (aka owner can access its own reviews even if private)</li>
     * </ul>
     */
    private Result<Void, String> validateAccess(
            User user,
            Long bookId,
            Long reviewId,
            ReviewViewResponseDTO review
    ) {
        Optional<BookSearchResultDTO> bookResult = bookService.findBookById(bookId);
        if (bookResult.isEmpty())
            // TODO: redirect to unknown book page
            return new Result.Err<>("redirect:/");
        else if (!review.bookId().equals(bookId))
            return new Result.Err<>(
                    "redirect:/books/%d/reviews/%d?error=%s&context=%s".formatted(
                            review.bookId(),
                            reviewId,
                            "INVALID_ASSOCIATION",
                            bookId
                    )
            );
        boolean isReviewOwner = user != null && user.getUsername().equals(review.username());
        if (review.isPublic() || isReviewOwner)
            return new Result.Ok<>(null);
        return new Result.Err<>("redirect:/books/%d".formatted(bookId));
    }

    /**
     * validates whether user can create a comment on a review thread page
     *
     * @param user     current user
     * @param bookId   book id
     * @param reviewId review id
     * @param review   review object that contains owner's username to validate ownership
     * @return A {@code Result.Ok} containing null if the user can access the review thread page.
     * Returns a {@code Result.Err} with a redirect URL if the review thread page:
     * <ul>
     *     <li>made by a private profile</li>
     *     <li>is private but not the owner (aka owner can access its own reviews even if private)</li>
     * </ul>
     */
    private Result<Void, String> validatePostAccess(User user, Long bookId, Long reviewId, ReviewViewResponseDTO review) {
        if (user == null) // cannot comment if anon
            return new Result.Err<>("redirect:/books/%d".formatted(bookId));
        return validateAccess(user, bookId, reviewId, review);
    }
}
