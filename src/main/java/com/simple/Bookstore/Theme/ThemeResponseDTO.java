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
}
