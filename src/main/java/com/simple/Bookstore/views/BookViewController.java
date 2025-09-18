package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewBookViewResponseDTO;
import com.simple.Bookstore.Review.ReviewRequestDTO;
import com.simple.Bookstore.Review.ReviewResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BookViewController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ThemeService themeService;
    private final ProfileService profileService;

    @GetMapping("/books/{bookId}")
    public String getBook(
            @PathVariable Long bookId,
            @ModelAttribute("reviewRequestDTO") ReviewRequestDTO reviewRequest,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        Optional<BookSearchResultDTO> bookResult = bookService.findBookById(bookId);
        if (bookResult.isEmpty())
            // TODO: redirect to unknown book page
            return "redirect:/";
        Page<ReviewBookViewResponseDTO> reviews = reviewService.findAllPublicOrOwnedReviewsByBookIdAsPage(
                bookId,
                user != null ? user.getProfile().getId() : null,
                pageable
        );
        Long reviewCount = reviewService.countByBookId(bookId);
        List<Long> savedBookIds = bookService.findSavedBooks(user)
                .stream()
                .map(BookSearchResultDTO::id)
                .toList();
        boolean alreadyReviewed = reviewService.isAlreadyReviewedByUser(bookId, user);

        model.addAttribute("book", bookResult.get());
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("savedBookIds", savedBookIds);
        model.addAttribute("alreadyReviewed", alreadyReviewed);
        model.addAttribute(
                "reviewRequestDTO",
                reviewRequest.isEmpty()
                        ? ReviewRequestDTO.empty()
                        : reviewRequest
        );
        return "book";
    }

    @PostMapping("/books/{bookId}/reviews")
    public String addReview(
            @PathVariable Long bookId,
            @Valid @ModelAttribute("reviewRequestDTO") ReviewRequestDTO reviewRequest,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (reviewRequest == null || reviewRequest.isEmpty())
            return "redirect:/books/" + bookId;

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        if (result.hasErrors())
            return "book";

        reviewService.createReview(user, bookId, reviewRequest);
        return "redirect:/books/" + bookId;
    }
}
