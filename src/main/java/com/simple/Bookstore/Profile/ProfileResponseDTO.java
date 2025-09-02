package com.simple.Bookstore.Profile;

public record ProfileResponseDTO(
        Long id,
        String username,
        String displayName,
        boolean isPublic
) {
}
