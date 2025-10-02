package com.simple.Bookstore.views.Theme;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.User.User;

public interface ThemeViewService {
    ThemeViewModel validatePageAccess(User user, Long themeId) throws ThemeNotFoundException;
}
