package com.simple.Bookstore.views.Theme;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.CssGenerator;
import com.simple.Bookstore.views.HeaderAndSidebarsModelAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeViewController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ThemeService themeService;
    private final ThemeViewService themeViewService;

    @GetMapping("/{themeId}")
    public String theme(
            @PathVariable("themeId") Long themeId,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        ThemeViewModel viewModel = themeViewService.validatePageAccess(user, themeId);
        model.addAttribute("viewModel", viewModel);
        model.addAttribute(
                "previewThemeStyle",
                CssGenerator.toInlineCss(
                        List.of(
                                viewModel.theme().base00(),
                                viewModel.theme().base01(),
                                viewModel.theme().base02(),
                                viewModel.theme().base03(),
                                viewModel.theme().base04(),
                                viewModel.theme().base05(),
                                viewModel.theme().base06(),
                                viewModel.theme().base07()
                        ),
                        "#theme-preview-render"
                )
        );
        return "theme";
    }
}
