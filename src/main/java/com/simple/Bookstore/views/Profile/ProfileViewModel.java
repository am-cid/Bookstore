package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.User.User;

public record ProfileViewModel(
        User user,
        ProfileResponseDTO profile
) {
}
