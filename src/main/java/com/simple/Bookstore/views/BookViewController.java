package com.simple.Bookstore.views;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewBookViewResponseDTO;
import com.simple.Bookstore.Review.ReviewRequestDTO;
import com.simple.Bookstore.Review.ReviewResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.Result;
import com.simple.Bookstore.views.Profile.ProfileEditModel;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            throw new BookNotFoundException(bookId);

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

    // returns the confirmation page
    @GetMapping("/books/{bookId}/reviews/{reviewId}/delete/confirm")
    public String confirmEditProfile(
            @PathVariable("bookId") Long bookId,
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal User user,
            Model model
    ) {

        // null checks
        Optional<BookSearchResultDTO> bookResult = bookService.findBookById(bookId);

        if (bookResult.isEmpty()){
            throw new BookNotFoundException(bookId);
        }
        ReviewResponseDTO existingReviewDTO = reviewService.findReviewById( reviewId );
        if( existingReviewDTO == null ){
            throw new ReviewNotFoundException(reviewId);
        }

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        // need properties to persist till the result call
        model.addAttribute("bookId", bookId);
        model.addAttribute("reviewId", reviewId);

        //! need to create the review delete confirm page
        return "review-delete-confirm-page";
    }

    //  review delete confirmation button call
    @PostMapping("/books/{bookId}/reviews/{reviewId}/delete/confirm")
    public String confirmEditProfile(
            @PathVariable("bookId") Long bookId,
            @PathVariable("reviewId") Long reviewId
    ) {
        return String.format("redirect:/books/%d/reviews/%d/delete/result", bookId, reviewId);
    }

    // review delete back button call
    @PostMapping(path = "/books/{bookId}/reviews/{reviewId}/delete/confirm", params = {"_action=back"})
    public String confirmEditProfileGoBack(
            @PathVariable("bookId") Long bookId,
            @PathVariable("reviewId") Long reviewId
    ) {
        return String.format("redirect:/books/%d", bookId);
    }

    @GetMapping("/books/{bookId}/reviews/{reviewId}/delete/result")
    public String deleteReview(Model model, @PathVariable("bookId") Long bookId, @PathVariable("reviewId") Long reviewId, @ModelAttribute("reviewRequestDTO") ReviewRequestDTO reviewRequest,
            @AuthenticationPrincipal User user
    ) {
        HeaderAndSidebarsModelAttributes.defaults( user,model,bookService,reviewService,themeService );

        // deleting review
        reviewService.deleteReview( user, reviewId );
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        //! need to create the review delete result page
        return "review-delete-result";
    }
}
