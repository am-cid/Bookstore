package com.simple.Bookstore.Profile;

import com.simple.Bookstore.User.User;

public interface ProfileService {
    ProfileResponseDTO findByUsername(String username, User user);
}
