package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Theme.ThemeResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProfileViewThemesModel(
        Page<ThemeResponseDTO> profileThemes,
        ThemeResponseDTO currentUserTheme,
        List<Long> currentUserSavedThemeIds
) {
}
