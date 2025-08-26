package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ThemeService themeService;

    @GetMapping("/")
    public String showHomePage(
            Model model,
            @AuthenticationPrincipal User user
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        // center content data
        List<BookSearchResultDTO> latestBooks = bookService.findLatestNBooks(10); // Find more than needed to fill all sections
        model.addAttribute("bannerBooks", latestBooks.subList(0, Math.min(6, latestBooks.size())));

        // TODO: format below as "2 days ago. <username> just posted <book-title>. <description: 50 char cutoff>"
        // TODO: model.addAttribute("hotNewsBooks", latestBooks.subList(0, Math.min(2, latestBooks.size())));

        model.addAttribute("latestUploads", latestBooks.subList(0, Math.min(4, latestBooks.size())));
        model.addAttribute("availableBooks", bookService.findRelevantBooks());

        return "index";
    }
}
