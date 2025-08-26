package com.simple.Bookstore.Profile;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.User.User;

public interface ProfileService {
    ProfileResponseDTO findByUsername(String username, User user);

    ProfileResponseDTO setTheme(Long id, User user) throws UnauthorizedException, ThemeNotFoundException;

    void unsetThemeAndUseDefaultTheme(User user) throws UnauthorizedException;
}
