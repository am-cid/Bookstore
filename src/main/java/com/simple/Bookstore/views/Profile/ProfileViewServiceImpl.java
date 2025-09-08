package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Comment.CommentResponseDTO;
import com.simple.Bookstore.Comment.CommentService;
import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Review.ReviewViewResponseDTO;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserDeleteRequestDTO;
import com.simple.Bookstore.User.UserService;
import com.simple.Bookstore.utils.FormRequest;
import com.simple.Bookstore.utils.ProfileMapper;
import com.simple.Bookstore.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileViewServiceImpl implements ProfileViewService {
    private final UserService userService;
    private final ThemeService themeService;
    private final ReviewService reviewService;
    private final CommentService commentService;

    @Override
    public Result<Pair<ProfileViewModel, ProfileViewThemesModel>, String> buildProfileViewThemes(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UserNotFoundException {
        if (currentUser == null && pathUsername.equals("me"))
            return new Result.Err<>("redirect:/");

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());

        Optional<String> redirect = cannotAccessPrivateProfile(currentUser, pathUsername, foundProfile);
        if (redirect.isPresent())
            return new Result.Err<>(redirect.get());

        Page<ThemeResponseDTO> ownedThemes = themeService.findThemesByUser(foundUser, pageable);
        ThemeResponseDTO usedTheme = themeService.findUsedTheme(currentUser);
        List<Long> savedThemeIds = themeService
                .findSavedThemes(currentUser)
                .stream()
                .map(ThemeResponseDTO::id)
                .toList();
        return new Result.Ok<>(Pair.of(
                new ProfileViewModel(foundUser, foundProfile),
                new ProfileViewThemesModel(ownedThemes, usedTheme, savedThemeIds)
        ));
    }

    @Override
    public Result<Pair<ProfileViewModel, ProfileViewReviewsModel>, String> buildProfileViewReviews(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UserNotFoundException {
        if (currentUser == null && pathUsername.equals("me"))
            return new Result.Err<>("redirect:/");

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());

        Optional<String> redirect = cannotAccessPrivateProfile(currentUser, pathUsername, foundProfile);
        if (redirect.isPresent())
            return new Result.Err<>(redirect.get());

        Page<ReviewViewResponseDTO> reviews = reviewService.findAllReviewsByUser(foundUser, pageable);
        return new Result.Ok<>(Pair.of(
                new ProfileViewModel(foundUser, foundProfile),
                new ProfileViewReviewsModel(reviews)
        ));
    }

    @Override
    public Result<Pair<ProfileViewModel, ProfileViewCommentsModel>, String> buildProfileViewComments(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UserNotFoundException {
        if (currentUser == null && pathUsername.equals("me"))
            return new Result.Err<>("redirect:/");

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());

        Optional<String> redirect = cannotAccessPrivateProfile(currentUser, pathUsername, foundProfile);
        if (redirect.isPresent())
            return new Result.Err<>(redirect.get());

        Page<CommentResponseDTO> comments = commentService.findAllCommentsByUser(foundUser, pageable);
        return new Result.Ok<>(Pair.of(
                new ProfileViewModel(foundUser, foundProfile),
                new ProfileViewCommentsModel(comments)
        ));
    }

    @Override
    public Result<ProfileEditModel, String> buildProfileEditView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws UserNotFoundException {
        Result<User, String> validUserOrRedirect = validateEditDeleteAccess(currentUser, pathUsername);
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        User foundUser = validUserOrRedirect.unwrap();
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        ProfileEditRequestDTO profileEditRequestDTO =
                isUninitialized(editRequest)
                        ? ProfileMapper.profileResponseDtoToFreshEditRequestDTO(foundProfile)
                        : editRequest;
        return new Result.Ok<>(new ProfileEditModel(
                foundUser,
                foundProfile,
                profileEditRequestDTO
        ));
    }

    @Override
    public Result<ProfileEditModel, String> validateEditAccess(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws UserNotFoundException {
        Result<User, String> validUserOrRedirect = validateEditDeleteAccess(currentUser, pathUsername);
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        User foundUser = validUserOrRedirect.unwrap();
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        return new Result.Ok<>(new ProfileEditModel(
                foundUser,
                foundProfile,
                editRequest
        ));
    }

    @Override
    public Result<ProfileEditModel, String> validateEditAccessAndRequest(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws UserNotFoundException, IllegalStateException {
        Result<User, String> validUserOrRedirect = validateEditDeleteAccessAndRequest(
                currentUser,
                pathUsername,
                editRequest,
                "/edit"
        );
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        User foundUser = validUserOrRedirect.unwrap();
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        return new Result.Ok<>(new ProfileEditModel(
                foundUser,
                foundProfile,
                editRequest
        ));
    }

    @Override
    public Result<ProfileDeleteModel, String> validateDeleteAccess(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    ) throws UserNotFoundException {
        Result<User, String> validUserOrRedirect = validateEditDeleteAccess(currentUser, pathUsername);
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        User foundUser = validUserOrRedirect.unwrap();
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        return new Result.Ok<>(new ProfileDeleteModel(
                foundUser,
                foundProfile,
                deleteRequest
        ));
    }

    @Override
    public Result<ProfileDeleteModel, String> validateDeleteAccessAndRequest(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    ) throws UserNotFoundException, IllegalStateException {
        Result<User, String> validUserOrRedirect = validateEditDeleteAccessAndRequest(
                currentUser,
                pathUsername,
                deleteRequest,
                "/delete"
        );
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        User foundUser = validUserOrRedirect.unwrap();
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        return new Result.Ok<>(new ProfileDeleteModel(
                foundUser,
                foundProfile,
                deleteRequest
        ));
    }

    // HELPERS

    /**
     * determines if user can access a specific profile based on its privacy settings.
     * <p>
     * This method checks three conditions for access:
     * <ul>
     * <li>profile requested is the authenticated user's own profile ("me").</li>
     * <li>target profile is publicly visible.</li>
     * <li>currently authenticated user is the owner of the target profile, public or private.</li>
     * </ul>
     *
     * @param user     currently authenticated user, or {@code null} if anonymous.
     * @param username username from the path, which can be a specific username or "me".
     * @param profile  {@link ProfileResponseDTO} of the profile being requested.
     * @return An {@link Optional#empty()} if the user can access the profile. Returns an {@link Optional}
     * containing a redirect URL string if access is denied.
     */
    private Optional<String> cannotAccessPrivateProfile(
            User user,
            String username,
            ProfileResponseDTO profile
    ) {

        if (username.equals("me")
                || profile.isPublic()
                || user != null && user.getProfile().getId().equals(profile.id())
        )
            return Optional.empty();

        // TODO: redirect to unknown user page
        return Optional.of("redirect:/");
    }


    /**
     * determines if the {@code currentUser} has permission to edit/delete the profile specified by
     * the {@code pathUsername}.
     * </p>
     *
     * @param currentUser  currently authenticated user, or {@code null} if anonymous.
     * @param pathUsername username from the URL path, which can be a specific username or "me".
     * @return A {@link Result.Ok} containing the user to be edited if the request is valid.
     * A user is never anonymous when request is valid so use this information for whatever.
     * <p>
     * Returns a {@link Result.Err} with a redirect URL if the request is invalid,
     * or the user is unauthorized.
     * @throws UserNotFoundException if a user specified by {@code pathUsername} (not "me") does not exist.
     */
    private Result<User, String> validateEditDeleteAccess(
            User currentUser,
            String pathUsername
    ) throws UserNotFoundException {
        // anon /userA/action -> no
        // anon no request /userA/action -> no
        if (currentUser == null && !pathUsername.equals("me"))
            return new Result.Err<>("redirect:/profile/" + pathUsername);
        // anon /me/action -> no
        // anon no request /me/action -> no
        if (currentUser == null)
            return new Result.Err<>("redirect:/");
        // userB /userA/action -> no
        // userB no request /userA/action -> no
        if (!pathUsername.equals("me") && !currentUser.getUsername().equals(pathUsername))
            return new Result.Err<>("redirect:/profile/" + pathUsername);
        // userA /userA/action -> ok
        // userA no request /userA/action -> ok
        // userA /me/action -> ok
        // userB /me/action -> ok
        // userA no request /me/action -> ok
        // userB no request /me/action -> ok

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));

        return new Result.Ok<>(foundUser);
    }

    /**
     * Calls {@link ProfileViewService#validateEditAccess()} to determine if the {@code currentUser}
     * has permission to edit/delete the profile specified by the {@code pathUsername}. also checks
     * if the {@code editRequest} DTO is populated by the form in the previous edit step.
     * </p>
     *
     * @param currentUser  currently authenticated user, or {@code null} if anonymous.
     * @param pathUsername username from the URL path, which can be a specific username or "me".
     * @param formRequest  request body from form data containing either edit or delete data.
     *                     May be uninitialized or {@code null} if the request is not a form submission.
     * @return A {@code Result.Ok} containing the user to be edited if the request is valid.
     * Returns a {@code Result.Err} with a redirect URL if the request is invalid,
     * the user is unauthorized, or the DTO is uninitialized.
     * @throws UserNotFoundException if a user specified by {@code pathUsername} (not "me") does not exist.
     */
    private Result<User, String> validateEditDeleteAccessAndRequest(
            User currentUser,
            String pathUsername,
            FormRequest formRequest,
            String action
    ) throws IllegalStateException {
        switch (action) {
            case "/edit":
            case "/delete":
                break;
            default:
                throw new IllegalStateException("Invalid action: " + action);
        }

        Result<User, String> initialResult = validateEditDeleteAccess(currentUser, pathUsername);
        if (initialResult.isErr())
            return initialResult;

        // userA /userA/edit -> ok
        // userA /me/edit -> ok
        // userB /me/edit -> ok
        // userA no request /userA/edit -> no
        // userA no request /me/edit -> no
        // userB no request /me/edit -> no
        if (isUninitialized(formRequest))
            return new Result.Err<>("redirect:/profile/" + pathUsername + action);

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));

        return new Result.Ok<>(foundUser);
    }

    /**
     * @param formRequest the requestDTO for forms
     * @return whether initialized or not
     */
    private boolean isUninitialized(FormRequest formRequest) {
        return formRequest == null || formRequest.isUninitialized();
    }
}
