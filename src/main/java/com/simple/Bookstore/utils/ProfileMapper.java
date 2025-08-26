package com.simple.Bookstore.utils;

import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileResponseDTO;

import java.util.stream.Collectors;

public class ProfileMapper {
    public static ProfileResponseDTO profileToResponseDTO(Profile profile) {
        return new ProfileResponseDTO(
                profile.getDisplayName(),
                profile.getOwnedThemes()
                        .stream()
                        .map(ThemeMapper::themeToResponseDTO)
                        .collect(Collectors.toSet()),
                profile.getSavedThemes()
                        .stream()
                        .map(ThemeMapper::themeToResponseDTO)
                        .collect(Collectors.toSet()),
                profile.getSavedBooks()
                        .stream()
                        .map(BookMapper::bookToSearchResultDTO)
                        .collect(Collectors.toSet())

    public static ProfileResponseDTO projectionToResponseDTO(ProfileProjection projection) {
        return new ProfileResponseDTO(
                projection.getId(),
                projection.getUsername(),
                projection.getDisplayName()
        );
    }
}
