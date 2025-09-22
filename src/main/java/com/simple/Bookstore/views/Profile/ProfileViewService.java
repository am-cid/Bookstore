package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserDeleteRequestDTO;
import com.simple.Bookstore.utils.Result;
import com.simple.Bookstore.views.SharedModels.ViewThemesModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

public interface ProfileViewService {
    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<Pair<ProfileViewModel, ViewThemesModel>, String> buildProfileViewThemes(
            User currentUser,
            String pathUsername,
            Pageable pageable
    );

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<Pair<ProfileViewModel, ProfileViewReviewsModel>, String> buildProfileViewReviews(
            User currentUser,
            String pathUsername,
            Pageable pageable
    );

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param pageable     ownedTheme paging
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<Pair<ProfileViewModel, ProfileViewCommentsModel>, String> buildProfileViewComments(
            User currentUser,
            String pathUsername,
            Pageable pageable
    );

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param editRequest  edit request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<ProfileEditModel, String> buildProfileEditView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    );

    /**
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param editRequest  edit request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<ProfileEditModel, String> validateEditAccess(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    );

    /**
     * validates request that came from /profile/me/edit -> /profile/me/edit/confirm
     *
     * @param currentUser  currently authenticated user. null if anonymous
     * @param pathUsername username of profile being accessed
     * @param editRequest  edit request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     */
    Result<ProfileEditModel, String> validateEditAccessAndRequest(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws IllegalStateException;

    /**
     * @param currentUser   currently authenticated user. null if anonymous
     * @param pathUsername  username of profile being accessed
     * @param deleteRequest delete request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     * @throws UserNotFoundException when user with target username is not found
     */
    Result<ProfileDeleteModel, String> validateDeleteAccess(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    );


    /**
     * @param currentUser   currently authenticated user. null if anonymous
     * @param pathUsername  username of profile being accessed
     * @param deleteRequest delete request made by user
     * @return view model if ok. return a "redirect:/some/path" or a "template-name" if any failed any checks
     * @throws UserNotFoundException when user with target username is not found
     */
    Result<ProfileDeleteModel, String> validateDeleteAccessAndRequest(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    ) throws IllegalStateException;
}
