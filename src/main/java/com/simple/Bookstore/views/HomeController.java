package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Review.ReviewResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.utils.PostedDateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping("/")
    public String showHomePage(Model model) {
        // center content data
        List<BookSearchResultDTO> latestBooks = bookService.findLatestNBooks(10); // Find more than needed to fill all sections
        model.addAttribute("bannerBooks", latestBooks.subList(0, Math.min(6, latestBooks.size())));

        // TODO: format below as "2 days ago. <username> just posted <book-title>. <description: 50 char cutoff>"
        // TODO: model.addAttribute("hotNewsBooks", latestBooks.subList(0, Math.min(2, latestBooks.size())));

        model.addAttribute("latestReleases", latestBooks.subList(0, Math.min(4, latestBooks.size())));
        model.addAttribute("availableTitles", bookService.findRelevantBooks());

        // left sidebar data
        System.out.println(Arrays.stream(Genre.values()).toList());
        model.addAttribute("genres", Arrays.stream(Genre.values()).toList());
        model.addAttribute("authors", bookService.findDistinctAuthors());
        List<ReviewResponseDTO> latestReview = reviewService.findLatestNReviews(1); // A new service method
        if (latestReview.isEmpty()) {
            model.addAttribute("latestReview", null);
            model.addAttribute("latestReviewDate", null);
        } else {
            ReviewResponseDTO latestReviewDTO = latestReview.getFirst();
            String formattedDate = PostedDateFormatter
                    .formatTimeAgo(latestReviewDTO.date(), latestReviewDTO.edited());
            model.addAttribute("latestReview", latestReviewDTO);
            model.addAttribute("latestReviewDate", formattedDate);
        }

        // right sidebar data
        model.addAttribute("top5Books", bookService.findTopNRatedBooks(5)); // A new service method

        return "index";
    }
}
