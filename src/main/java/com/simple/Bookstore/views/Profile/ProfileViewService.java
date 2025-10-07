package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Exceptions.ForbiddenException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserDeleteRequestDTO;
import com.simple.Bookstore.utils.Result;
import com.simple.Bookstore.views.SharedModels.ViewThemesAsPageModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

public interface ProfileViewService {
    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view models
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    Pair<ProfileViewModel, ViewThemesAsPageModel> buildProfileViewThemes(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UnauthorizedException, UserNotFoundException;

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view models
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    Pair<ProfileViewModel, ProfileViewReviewsModel> buildProfileViewReviews(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UnauthorizedException, UserNotFoundException;

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view models
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    Pair<ProfileViewModel, ProfileViewCommentsModel> buildProfileViewComments(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UnauthorizedException, UserNotFoundException;

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view models
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    Pair<ProfileViewModel, ViewThemesAsPageModel> buildProfileViewSavedThemes(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException;

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view models
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    Pair<ProfileViewModel, ProfileViewSavedBooksModel> buildProfileViewSavedBooks(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException;

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param editRequest  edit request made by user
     * @return view model
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    ProfileEditModel buildProfileEditView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException;

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param editRequest  edit request made by user
     * @return view model
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    ProfileEditModel validateEditAccess(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException;

    /**
     * validates request that came from /profile/me/edit -> /profile/me/edit/confirm
     *
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param editRequest  edit request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if edit request is empty
     * @throws IllegalStateException internal error (when action is not <code>/edit</code> or <code>/delete</code>)
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    Result<ProfileEditModel, String> validateEditAccessAndRequest(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws ForbiddenException, IllegalStateException, UnauthorizedException, UserNotFoundException;

    /**
     * @param currentUser   currently authenticated user. null if anonymous
     * @param pathUsername  username of profile being accessed
     * @param deleteRequest delete request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    ProfileDeleteModel validateDeleteAccess(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException;


    /**
     * @param currentUser   currently authenticated user. null if anonymous
     * @param pathUsername  username of profile being accessed
     * @param deleteRequest delete request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if delete request is empty
     * @throws IllegalStateException internal error (when action is not <code>/edit</code> or <code>/delete</code>)
     * @throws UnauthorizedException when you need to log in to access /profile/me
     * @throws UserNotFoundException when user with username is not found
     */
    Result<ProfileDeleteModel, String> validateDeleteAccessAndRequest(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    ) throws ForbiddenException, IllegalStateException, UnauthorizedException, UserNotFoundException;
}
