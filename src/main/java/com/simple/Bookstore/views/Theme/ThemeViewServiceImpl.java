package com.simple.Bookstore.views.Theme;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.CssGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThemeViewServiceImpl implements ThemeViewService {
    private final ThemeService themeService;

    @Override
    public ThemeViewModel validatePageAccess(User user, Long themeId) throws ThemeNotFoundException {
        ThemeResponseDTO theme = validateAccess(user, themeId);
        return new ThemeViewModel(
                theme,
                themeService.findUsedTheme(user),
                themeService.findSavedThemeIds(user),
                CssGenerator.toInlineCss(
                        List.of(
                                theme.base00(),
                                theme.base01(),
                                theme.base02(),
                                theme.base03(),
                                theme.base04(),
                                theme.base05(),
                                theme.base06(),
                                theme.base07()
                        ),
                        "#theme-preview-render"
                )
        );
    }

    // HELPERS
    private ThemeResponseDTO validateAccess(User user, Long themeId) throws ThemeNotFoundException {
        Optional<ThemeResponseDTO> themeOpt = themeService.findPublishedOrOwnedThemeById(user, themeId);
        if (themeOpt.isEmpty())
            throw new ThemeNotFoundException(themeId);
        return themeOpt.get();
    }
}
