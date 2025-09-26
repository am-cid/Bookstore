package com.simple.Bookstore.views.Theme;

import com.simple.Bookstore.Theme.ThemeResponseDTO;

import java.util.List;

public record ThemeViewModel(
        ThemeResponseDTO theme,
        ThemeResponseDTO usedTheme,
        List<Long> savedThemeIds
) {
}
