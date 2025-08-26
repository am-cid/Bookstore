package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Review.ReviewResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.CssGenerator;
import com.simple.Bookstore.utils.PostedDateFormatter;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

public class HeaderAndSidebarsModelAttributes {
    /**
     * initializes the needed data for header, and the two sidebars
     *
     * @param model model to add attributes to
     */
    public static void defaults(
            User user,
            Model model,
            BookService bookService,
            ReviewService reviewService,
            ThemeService themeService
    ) {
        model.addAttribute("user", user);
        ThemeResponseDTO themeUsed = themeService.findUsedTheme(user);
        List<String> themeColors = themeUsed != null
                ? List.of(
                themeUsed.base00(),
                themeUsed.base01(),
                themeUsed.base02(),
                themeUsed.base03(),
                themeUsed.base04(),
                themeUsed.base05(),
                themeUsed.base06(),
                themeUsed.base07())
                : null;
        String themeUsedAsInlineCss = CssGenerator.toInlineCss(themeColors);
        model.addAttribute("_overrideTheme", themeUsedAsInlineCss);
        model.addAttribute("_overrideThemeUsed", themeUsed);
        // header data
        model.addAttribute(
                "logoLetters",
                SiteLogo.getLogoLettersModelAttributes("BOOKSTORE")
        );

        // left sidebar data
        model.addAttribute("genres", Arrays.stream(Genre.values()).toList());
        model.addAttribute("authors", bookService.findDistinctAuthors());
        List<ReviewResponseDTO> latestReview = reviewService.findLatestNReviews(1); // A new service method
        if (latestReview.isEmpty()) {
            model.addAttribute("_latestReview", null);
            model.addAttribute("_latestReviewDate", null);
        } else {
            ReviewResponseDTO latestReviewDTO = latestReview.getFirst();
            String formattedDate = PostedDateFormatter
                    .formatTimeAgo(latestReviewDTO.date(), latestReviewDTO.edited());
            model.addAttribute("_latestReview", latestReviewDTO);
            model.addAttribute("_latestReviewDate", formattedDate);
        }

        // right sidebar data
        model.addAttribute("top5Books", bookService.findTopNRatedBooks(5)); // A new service method
    }
}
