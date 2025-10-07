package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookPreviewDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.views.SharedModels.ViewThemesAsListModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeViewController {
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
        List<BookPreviewDTO> latestBooks = bookService.findLatestNBooks(10);
        model.addAttribute("bannerBooks", latestBooks.subList(0, Math.min(6, latestBooks.size())));

        model.addAttribute(
                "viewThemesListModel",
                new ViewThemesAsListModel(
                        themeService.findLatestNPublishedOrOwnedThemes(user, 5),
                        themeService.findUsedTheme(user),
                        themeService.findSavedThemeIds(user)
                ));

        model.addAttribute("latestUploads", latestBooks.subList(0, Math.min(4, latestBooks.size())));
        model.addAttribute("availableBooks", bookService.findRelevantBooks(12));

        return "index";
    }
}
