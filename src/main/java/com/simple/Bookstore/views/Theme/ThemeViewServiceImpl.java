package com.simple.Bookstore.views.Theme;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.ColorUtils;
import com.simple.Bookstore.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThemeViewServiceImpl implements ThemeViewService {
    private final ThemeService themeService;

    @Override
    public Result<ThemeViewModel, String> validatePageAccess(User user, Long themeId) throws ThemeNotFoundException {
        Result<ThemeResponseDTO, String> validOrRedirect = validateAccess(user, themeId);
        if (validOrRedirect.isErr())
            return new Result.Err<>(validOrRedirect.unwrapErr());
        ThemeResponseDTO theme = validOrRedirect.unwrap();
        List<String> interpolatedColors = ColorUtils
                .getInterpolatedColors(
                        Color.decode("#" + theme.base03()),
                        Color.decode("#" + theme.base04()),
                        27
                )
                .subList(1, 26);
        return new Result.Ok<>(new ThemeViewModel(
                theme,
                themeService.findUsedTheme(user),
                themeService.findSavedThemeIds(user)
        ));
    }

    // HELPERS
    private Result<ThemeResponseDTO, String> validateAccess(User user, Long themeId) {
        Optional<ThemeResponseDTO> themeOpt = themeService.findPublishedOrOwnedThemeById(user, themeId);
        if (themeOpt.isEmpty())
            // TODO: redirect to unknown theme page
            return new Result.Err<>("redirect:/");
        return new Result.Ok<>(themeOpt.get());
    }
}
