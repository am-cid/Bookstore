package com.simple.Bookstore.views.CreateTheme;

import com.simple.Bookstore.Theme.ThemeResponseDTO;

import java.util.List;

public record CreatedThemeViewModel(
        ThemeResponseDTO createdTheme,
        String previewThemeCss,
        ThemeResponseDTO currentUserTheme,
        List<Long> currentUserSavedThemeIds
) {
}
