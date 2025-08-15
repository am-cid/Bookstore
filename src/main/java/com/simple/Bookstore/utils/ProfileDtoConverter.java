package com.simple.Bookstore.utils;

import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileResponseDTO;

import java.util.stream.Collectors;

public class ProfileDtoConverter {
    public static ProfileResponseDTO profileToResponseDTO(Profile profile) {
        return new ProfileResponseDTO(
                profile.getDisplayName(),
                profile.getOwnedThemes()
                        .stream()
                        .map(ThemeDtoConverter::themeToResponseDTO)
                        .collect(Collectors.toSet()),
                profile.getSavedThemes()
                        .stream()
                        .map(ThemeDtoConverter::themeToResponseDTO)
                        .collect(Collectors.toSet()),
                profile.getSavedBooks()
                        .stream()
                        .map(BookDtoConverter::bookToSearchResultDTO)
                        .collect(Collectors.toSet())
        );
    }
}
