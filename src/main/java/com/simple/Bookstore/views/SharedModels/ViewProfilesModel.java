package com.simple.Bookstore.views.SharedModels;

import com.simple.Bookstore.Profile.ProfileResponseDTO;
import org.springframework.data.domain.Page;

public record ViewProfilesModel(
        Page<ProfileResponseDTO> profiles
) {
}
