package com.simple.Bookstore.views.CreateTheme;

import com.simple.Bookstore.Exceptions.ForbiddenException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Theme.ThemeRequestDTO;
import com.simple.Bookstore.User.User;

public interface CreateThemeViewService {
    /**
     * Validates whether user can access the create-theme page
     *
     * @param user            current user
     * @param themeRequestDTO request from form
     * @return inline css of style
     */
    String validatePageAccess(
            User user,
            ThemeRequestDTO themeRequestDTO
    ) throws UnauthorizedException;

    /**
     * Validates whether user's theme create request is valid
     *
     * @param user            current user
     * @param themeRequestDTO request from form
     * @return inline css of style
     */
    String validateCreateRequest(
            User user,
            ThemeRequestDTO themeRequestDTO
    ) throws ForbiddenException, UnauthorizedException;

    /**
     * Validates whether user's theme create request is valid.
     * Then creates theme, returning information whether
     *
     * @param user            current user
     * @param themeRequestDTO request from form
     * @return inline css of style
     */
    CreatedThemeViewModel validateCreateRequestThenCreateTheme(
            User user,
            ThemeRequestDTO themeRequestDTO
    ) throws ForbiddenException, UnauthorizedException;

    /**
     * Validates whether user's theme create request is valid
     *
     * @param user            current user
     * @param themeRequestDTO request from form
     * @return inline css of style
     */
    CreatedThemeViewModel validateCreateRequestAndReturnViewModel(
            User user,
            ThemeRequestDTO themeRequestDTO,
            Long createdThemeId
    ) throws ForbiddenException, IllegalStateException, UnauthorizedException;
}
