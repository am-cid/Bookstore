package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Comment.CommentRequestDTO;
import com.simple.Bookstore.Comment.CommentReviewViewResponseDTO;
import com.simple.Bookstore.Comment.CommentService;
import com.simple.Bookstore.Review.ReviewRequestDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Review.ReviewViewResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews")
public class ReviewViewController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ThemeService themeService;
    private final CommentService commentService;

    @PostMapping
    public String addReview(
            @PathVariable Long bookId,
            @Valid @ModelAttribute("reviewRequestDTO") ReviewRequestDTO reviewRequest,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (reviewRequest == null || reviewRequest.isEmpty())
            return "redirect:/books/%d".formatted(bookId);

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        if (result.hasErrors())
            return "book";

        reviewService.createReview(user, bookId, reviewRequest);
        return "redirect:/books/%d".formatted(bookId);
    }

    @GetMapping("/{reviewId}")
    public String showReview(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequest,
            @AuthenticationPrincipal User user,
            Model model,
            Pageable pageable
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        Optional<BookSearchResultDTO> bookResult = bookService.findBookById(bookId);
        if (bookResult.isEmpty())
            // TODO: redirect to unknown book page
            return "redirect:/";
        ReviewViewResponseDTO review = reviewService.findReviewViewById(reviewId);
        Page<CommentReviewViewResponseDTO> comments = commentService.findAllPublicOrOwnedCommentsByReviewIdAsPage(reviewId, user, pageable);

        model.addAttribute("bookId", bookId);
        model.addAttribute("review", review);
        model.addAttribute("comments", comments);
        model.addAttribute(
                "commentRequestDTO",
                commentRequest.isEmpty()
                        ? CommentRequestDTO.empty()
                        : commentRequest
        );
        return "book-review";
    }

    @PostMapping("/{reviewId}/comments")
    public String addComment(
            @PathVariable Long bookId,
            @PathVariable Long reviewId,
            @Valid @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequest,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model,
            Pageable pageable
    ) {
        if (commentRequest == null || commentRequest.isEmpty() || user == null)
            return "redirect:/books/%d/reviews/%d".formatted(bookId, reviewId);

        if (result.hasErrors()) {
            HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
            // TODO: need to maintain pageable so i either need to handle the href of form
            ReviewViewResponseDTO review = reviewService.findReviewViewById(reviewId);
            Page<CommentReviewViewResponseDTO> comments = commentService.findAllPublicOrOwnedCommentsByReviewIdAsPage(reviewId, user, pageable);

            model.addAttribute("bookId", bookId);
            model.addAttribute("review", review);
            model.addAttribute("comments", comments);
            return "book-review";
        }

        commentService.createComment(user, reviewId, commentRequest);
        return "redirect:/books/%d/reviews/%d".formatted(bookId, reviewId);
    }

}
