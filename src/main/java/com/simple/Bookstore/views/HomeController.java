package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Review.ReviewResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.CssGenerator;
import com.simple.Bookstore.utils.PostedDateFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Slf4j
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
        model.addAttribute("user", user);
        ThemeResponseDTO themeUsed = themeService.findThemeUsed(user);
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
                getLogoLettersModelAttributes("BOOKSTORE")
        );

        // center content data
        List<BookSearchResultDTO> latestBooks = bookService.findLatestNBooks(10); // Find more than needed to fill all sections
        model.addAttribute("bannerBooks", latestBooks.subList(0, Math.min(6, latestBooks.size())));

        // TODO: format below as "2 days ago. <username> just posted <book-title>. <description: 50 char cutoff>"
        // TODO: model.addAttribute("hotNewsBooks", latestBooks.subList(0, Math.min(2, latestBooks.size())));

        model.addAttribute("latestUploads", latestBooks.subList(0, Math.min(4, latestBooks.size())));
        model.addAttribute("availableBooks", bookService.findRelevantBooks());

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

        return "index";
    }

    private List<Map<String, Object>> getLogoLettersModelAttributes(String siteName) {
        char[] letters = siteName.toCharArray();
        List<Map<String, Object>> attributes = new ArrayList<>();
        for (int i = 0; i < letters.length; i++) {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("char", String.valueOf(letters[i]));
            attribute.put("id", "logo-letter-" + i);
            int rotate = switch (i) {
                case 1,
                     7 ->
                        -10;
                case 2 ->
                        10;
                case 4 ->
                        5;
                case 5 ->
                        -5;
                default ->
                        0;
            };
            attribute.put("rotate", rotate);
            int yOffset = switch (i) {
                case 1,
                     6 ->
                        2;
                case 2,
                     7 ->
                        -2;
                case 3 ->
                        1;
                case 5 ->
                        -3;
                default ->
                        0;
            };
            attribute.put("y", yOffset);
            attribute.put("x", i * 38);
            attributes.add(attribute);
        }
        return attributes;
    }
}
