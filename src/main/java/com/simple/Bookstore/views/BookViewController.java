package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        Optional<BookSearchResultDTO> bookResult = bookService.findBookById(bookId);
        if (bookResult.isEmpty())
            // TODO: redirect to unknown book page
            return "redirect:/";
        Page<ReviewResponseDTO> reviews = reviewService.findAllReviewsByBookIdAsPage(bookId,
                user != null ? user.getProfile().getId() : null,
                pageable
        );
        Long reviewCount = reviewService.countByBookId(bookId);
        List<Long> savedBookIds = bookService.findSavedBooks(user)
                .stream()
                .map(BookSearchResultDTO::id)
                .toList();
        model.addAttribute("book", bookResult.get());
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("savedBookIds", savedBookIds);
        return "book";
    }
}
