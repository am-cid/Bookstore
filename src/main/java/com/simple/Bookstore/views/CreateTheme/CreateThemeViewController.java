package com.simple.Bookstore.views.CreateTheme;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeRequestDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.views.HeaderAndSidebarsModelAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/create-theme")
@RequiredArgsConstructor
public class CreateThemeViewController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ThemeService themeService;

    @GetMapping
    public String createTheme(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        return "create-theme";
    }
}
