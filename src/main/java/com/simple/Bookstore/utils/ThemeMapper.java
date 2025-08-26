package com.simple.Bookstore.utils;

import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Theme.Theme;
import com.simple.Bookstore.Theme.ThemeProjection;
import com.simple.Bookstore.Theme.ThemeRequestDTO;
import com.simple.Bookstore.Theme.ThemeResponseDTO;

public class ThemeMapper {
    public static ThemeResponseDTO themeToResponseDTO(Theme theme) {
        long userId = theme.getProfile() != null
                ? theme.getProfile().getId()
                : 0;
        String username = theme.getProfile() != null
                ? theme.getProfile().getUser().getUsername()
                : null;
        String displayName = theme.getProfile() != null
                ? theme.getProfile().getDisplayName()
                : null;
        return new ThemeResponseDTO(
                theme.getId(),
                theme.getName(),
                theme.getDescription(),
                userId,
                username,
                displayName,
                theme.getBase00(),
                theme.getBase01(),
                theme.getBase02(),
                theme.getBase03(),
                theme.getBase04(),
                theme.getBase05(),
                theme.getBase06(),
                theme.getBase07()
        );
    }

    public static Theme requestDtoToTheme(Profile profile, ThemeRequestDTO request) {
        Theme theme = new Theme();
        theme.setProfile(profile);
        theme.setName(request.name());
        theme.setDescription(request.description());
        theme.setPublished(request.published());
        theme.setBase00(request.base00());
        theme.setBase01(request.base01());
        theme.setBase02(request.base02());
        theme.setBase03(request.base03());
        theme.setBase04(request.base04());
        theme.setBase05(request.base05());
        theme.setBase06(request.base06());
        theme.setBase07(request.base07());
        return theme;
    }

    public static ThemeResponseDTO projectionToResponseDTO(ThemeProjection projection) {
        return new ThemeResponseDTO(
                projection.getId(),
                projection.getName(),
                projection.getDescription(),
                projection.getUserId(),
                projection.getUsername(),
                projection.getUserDisplayName(),
                projection.getBase00(),
                projection.getBase01(),
                projection.getBase02(),
                projection.getBase03(),
                projection.getBase04(),
                projection.getBase05(),
                projection.getBase06(),
                projection.getBase07()
        );
    }
}
