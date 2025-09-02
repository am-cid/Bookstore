package com.simple.Bookstore.utils;

import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileProjection;
import com.simple.Bookstore.Profile.ProfileResponseDTO;

public class ProfileMapper {
    public static ProfileResponseDTO profileToResponseDTO(Profile profile) {
        return new ProfileResponseDTO(
                profile.getId(),
                profile.getUser().getUsername(),
                profile.getDisplayName(),
                profile.isPublic()
        );
    }

    public static ProfileResponseDTO projectionToResponseDTO(ProfileProjection projection) {
        return new ProfileResponseDTO(
                projection.getId(),
                projection.getUsername(),
                projection.getDisplayName(),
                projection.getIsPublic()
        );
    }

    public static ProfileEditRequestDTO profileToFreshEditRequestDTO(Profile profile) {
        return new ProfileEditRequestDTO(
                profile.getUser().getUsername(),
                "",
                "",
                profile.getDisplayName(),
                profile.isPublic(),
                ""
        );
    }

    public static ProfileEditRequestDTO profileResponseDtoToFreshEditRequestDTO(ProfileResponseDTO profile) {
        return new ProfileEditRequestDTO(
                profile.username(),
                "",
                "",
                profile.displayName(),
                profile.isPublic(),
                ""
        );
    }
}
