package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.User.User;

public record ProfileEditModel(
        User user,
        ProfileResponseDTO profile,
        ProfileEditRequestDTO editRequest
) {
}
