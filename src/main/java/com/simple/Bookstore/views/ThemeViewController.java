package com.simple.Bookstore.views;

import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class ThemeViewController {
    private final ProfileService profileService;

    @PostMapping("/profile/theme")
    public RedirectView setTheme(
            @RequestParam("themeId") Long themeId,
            @AuthenticationPrincipal User user,
            @RequestHeader("Referer") String referer
    ) {
        profileService.setTheme(themeId, user);
        return new RedirectView(referer);
    }

    @PostMapping(path = "/profile/theme", params = {"_method=delete"})
    public RedirectView unsetThemeAndUseDefaultTheme(
            @AuthenticationPrincipal User user,
            @RequestHeader("Referer") String referer
    ) {
        profileService.unsetThemeAndUseDefaultTheme(user);
        return new RedirectView(referer);
    }

    @PostMapping("/profile/saved-themes")
    public RedirectView saveTheme(
            @RequestParam("themeId") Long themeId,
            @AuthenticationPrincipal User user,
            @RequestHeader("Referer") String referer
    ) {
        profileService.saveTheme(themeId, user);
        return new RedirectView(referer);
    }

    @PostMapping(path = "/profile/saved-themes", params = {"_method=delete"})
    public RedirectView unsaveTheme(
            @RequestParam("themeId") Long themeId,
            @AuthenticationPrincipal User user,
            @RequestHeader("Referer") String referer
    ) {
        profileService.unsaveTheme(themeId, user);
        return new RedirectView(referer);
    }
}
