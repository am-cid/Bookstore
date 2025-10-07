package com.simple.Bookstore.views.SharedModels;

import com.simple.Bookstore.Theme.ThemeResponseDTO;

import java.util.List;

public record ViewThemesAsListModel(
        List<ThemeResponseDTO> themes,
        ThemeResponseDTO currentUserTheme,
        List<Long> currentUserSavedThemeIds
) {
}
