package com.simple.Bookstore.views.Theme;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.Result;

public interface ThemeViewService {
    Result<ThemeViewModel, String> validatePageAccess(User user, Long themeId) throws ThemeNotFoundException;
}
