package com.simple.Bookstore.views.SharedModels;

import com.simple.Bookstore.Theme.ThemeResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public record ViewThemesModel(
        Page<ThemeResponseDTO> themes,
        ThemeResponseDTO currentUserTheme,
        List<Long> currentUserSavedThemeIds
) {
}
