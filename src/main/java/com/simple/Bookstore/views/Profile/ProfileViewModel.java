package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProfileViewModel(
        User user,
        ProfileResponseDTO profile,
        Page<ThemeResponseDTO> ownedThemes,
        ThemeResponseDTO usedTheme,
        List<Long> savedThemeIds
) {
}
