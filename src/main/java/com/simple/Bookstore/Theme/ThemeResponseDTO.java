package com.simple.Bookstore.Theme;

import java.time.LocalDateTime;

public record ThemeResponseDTO(
        Long id,
        String name,
        String description,
        Boolean published,
        LocalDateTime date,
        Long profileId,
        String username,
        String userDisplayName,
        String base00,
        String base01,
        String base02,
        String base03,
        String base04,
        String base05,
        String base06,
        String base07
) {
    public static ThemeResponseDTO Default() {
        return new ThemeResponseDTO(
                (long) -1,
                "Default Mangagagamer Theme",
                "default theme",
                LocalDateTime.now(),
                (long) -1,
                "admin",
                "admin",
                "A40E60",
                "FF0000",
                "F61590",
                "F88B9E",
                "FFD376",
                "AAFFB1",
                "FFE2E7",
                "FFF2D3"
        );
    }
}
