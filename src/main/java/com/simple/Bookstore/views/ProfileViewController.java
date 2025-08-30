package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
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

@Controller
@RequiredArgsConstructor
public class ProfileViewController {
    private final ProfileService profileService;
    private final BookService bookService;
    private final ThemeService themeService;
    private final ReviewService reviewService;

    @GetMapping("/profile/me")
    public String profile(
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) throws UnauthorizedException {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        ProfileResponseDTO profile = profileService.findOwn(user);
        Page<ThemeResponseDTO> createdThemes = themeService.findThemesByUser(user, pageable);

        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        model.addAttribute("ownedThemes", createdThemes);
        model.addAttribute(
                "usedTheme",
                themeService.findUsedTheme(user)
        );
        model.addAttribute(
                "savedThemeIds",
                themeService
                        .findSavedThemes(user)
                        .stream()
                        .map(ThemeResponseDTO::id)
                        .toList()
        );
        return "profile";
    }
}
