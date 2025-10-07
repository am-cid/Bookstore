package com.simple.Bookstore.views.CreateTheme;

import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Theme.ThemeRequestDTO;
import com.simple.Bookstore.User.User;

public record CreateThemeViewModel(
        User user,
        ProfileResponseDTO profile,
        ThemeRequestDTO themeRequest
) {
}
