package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserDeleteRequestDTO;

public record ProfileDeleteModel(
        User user,
        ProfileResponseDTO profile,
        UserDeleteRequestDTO deleteRequest
) {
}
