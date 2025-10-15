package com.simple.Bookstore.utils;

import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Theme.*;
import com.simple.Bookstore.User.User;
import jakarta.annotation.Nullable;

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
                theme.isPublished(),
                theme.getDate(),
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
                projection.getPublished(),
                projection.getDate(),
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

    /**
     * @param user
     * @param yamlScheme
     * @param published
     * @param customDescription String/null - leave empty to use yamlScheme.author as the description
     * @return
     */
    public static Theme base16YamlToTheme(
            User user,
            ThemeFromBase16Yaml yamlScheme,
            boolean published,
            String customDescription
    ) {
        Theme theme = new Theme();
        theme.setName(yamlScheme.scheme());
        theme.setDescription(
                customDescription == null || customDescription.isEmpty()
                        ? String.format("Maintained by %s", yamlScheme.author())
                        : customDescription
        );
        theme.setProfile(user.getProfile());
        theme.setPublished(published);
        theme.setBase00(yamlScheme.base00());
        theme.setBase01(yamlScheme.base01());
        theme.setBase02(yamlScheme.base02());
        theme.setBase03(yamlScheme.base03());
        theme.setBase04(yamlScheme.base04());
        theme.setBase05(yamlScheme.base05());
        theme.setBase06(yamlScheme.base06());
        theme.setBase07(yamlScheme.base07());
        return theme;
    }

    /**
     * @param yamlScheme
     * @param published
     * @param customName        String/null - leave empty to use yamlScheme.scheme as the theme name
     * @param customDescription String/null - leave empty to use yamlScheme.author as the theme description
     * @return
     */
    public static ThemeRequestDTO base16YamlToRequestDTO(
            ThemeFromBase16Yaml yamlScheme,
            boolean published,
            @Nullable String customName,
            @Nullable String customDescription
    ) {
        return new ThemeRequestDTO(
                customName == null || customName.isEmpty()
                        ? yamlScheme.scheme()
                        : customName,
                customDescription == null || customDescription.isEmpty()
                        ? String.format("Maintained by %s", yamlScheme.author())
                        : customDescription,
                published,
                yamlScheme.base00(),
                yamlScheme.base01(),
                yamlScheme.base02(),
                yamlScheme.base03(),
                yamlScheme.base04(),
                yamlScheme.base05(),
                yamlScheme.base06(),
                yamlScheme.base07()
        );
    }
}
