package com.simple.Bookstore.views.Review;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Comment.CommentRequestDTO;
import com.simple.Bookstore.Comment.CommentService;
import com.simple.Bookstore.Review.ReviewRequestDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.Result;
import com.simple.Bookstore.views.HeaderAndSidebarsModelAttributes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/reviews")
public class ReviewViewController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ThemeService themeService;
    private final CommentService commentService;
    private final ReviewViewService reviewViewService;

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
            @RequestParam(required = false) ReviewViewError error,
            @RequestParam(required = false) List<String> context,
            @AuthenticationPrincipal User user,
            @ModelAttribute("commentRequestDTO") CommentRequestDTO commentRequest,
            Model model,
            @PageableDefault Pageable pageable
    ) {
        Result<ReviewViewModel, String> viewResult = reviewViewService
                .validatePageAccess(user, bookId, reviewId, pageable);
        if (viewResult.isErr())
            return viewResult.unwrapErr();
        if (ReviewViewError.INVALID_ASSOCIATION.equals(error)) {
            StringBuilder errorMessage = new StringBuilder();
            if (!context.isEmpty())
                errorMessage.append("Review %d is not associated with Book %s. ".formatted(reviewId, context.getFirst()));
            errorMessage.append("You've been redirected to the correct page (Book %s).".formatted(bookId));
            model.addAttribute("errorMessage", errorMessage.toString());
        }

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute("viewModel", viewResult.unwrap());
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
            @PageableDefault Pageable pageable
    ) {
        if (commentRequest == null || commentRequest.isEmpty())
            return "redirect:/books/%d/reviews/%d".formatted(bookId, reviewId);
        Result<ReviewViewModel, String> viewResult = reviewViewService
                .validateCommentCreationAccess(user, bookId, reviewId, pageable);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        if (result.hasErrors()) {
            // TODO: need to maintain pageable so i need to handle the href of form by passing the pageable in the model
            HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
            model.addAttribute("viewModel", viewResult.unwrap());
            return "book-review";
        }

        commentService.createComment(user, reviewId, commentRequest);
        int totalPages = Math.ceilDiv(
                commentService.countAllPublicOrOwnedCommentsByReviewId(reviewId, user),
                pageable.getPageSize()
        );
        int lastPage = totalPages > 0 ? totalPages - 1 : 0;
        return "redirect:/books/%d/reviews/%d?page=%d".formatted(bookId, reviewId, lastPage);
    }

}
