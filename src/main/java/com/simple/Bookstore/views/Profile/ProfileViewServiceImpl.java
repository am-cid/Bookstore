package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserService;
import com.simple.Bookstore.utils.ProfileMapper;
import com.simple.Bookstore.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileViewServiceImpl implements ProfileViewService {
    private final UserService userService;
    private final ThemeService themeService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Result<ProfileViewModel, String> buildProfileView(
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
        ThemeResponseDTO usedTheme = themeService.findUsedTheme(foundUser);
        List<Long> savedThemeIds = themeService
                .findSavedThemes(foundUser)
                .stream()
                .map(ThemeResponseDTO::id)
                .toList();
        return new Result.Ok<>(new ProfileViewModel(
                foundUser,
                foundProfile,
                ownedThemes,
                usedTheme,
                savedThemeIds
        ));
    }

    @Override
    public Result<ProfileEditModel, String> buildProfileEditView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws UserNotFoundException {
        Result<User, String> validUserOrRedirect = validateEditAccess(currentUser, pathUsername);
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        User foundUser = validUserOrRedirect.unwrap();
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        ProfileEditRequestDTO profileEditRequestDTO =
                editRequest.isUninitializedForEditing()
                        ? ProfileMapper.profileResponseDtoToFreshEditRequestDTO(foundProfile)
                        : editRequest;
        return new Result.Ok<>(new ProfileEditModel(
                foundUser,
                foundProfile,
                profileEditRequestDTO
        ));
    }

    @Override
    public Result<ProfileEditModel, String> validateProfileEditView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws UserNotFoundException {
        Result<User, String> validUserOrRedirect = validateEditAccess(currentUser, pathUsername);
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
    public Result<ProfileEditRequestDTO, String> validateProfileEditConfirmView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws UserNotFoundException {
        Result<User, String> validUserOrRedirect = validateEditAccessAndRequest(currentUser, pathUsername, editRequest);
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        return new Result.Ok<>(editRequest);
    }

    @Override
    public Result<ProfileEditModel, String> buildProfileEditResultView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws UserNotFoundException {
        Result<User, String> validUserOrRedirect = validateEditAccess(currentUser, pathUsername);
        if (validUserOrRedirect.isErr())
            return new Result.Err<>(validUserOrRedirect.unwrapErr());

        User foundUser = validUserOrRedirect.unwrap();
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        assert !editRequest.isUninitializedForEditing();
        return new Result.Ok<>(new ProfileEditModel(
                foundUser,
                foundProfile,
                editRequest
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
     * determines if the {@code currentUser} has permission to edit the profile specified by the
     * {@code pathUsername}.
     * </p>
     *
     * @param currentUser  currently authenticated user, or {@code null} if anonymous.
     * @param pathUsername username from the URL path, which can be a specific username or "me".
     * @return A {@link Result.Ok} containing the user to be edited if the request is valid.
     * Returns a {@link Result.Err} with a redirect URL if the request is invalid,
     * the user is unauthorized, or the DTO is uninitialized.
     * @throws UserNotFoundException if a user specified by {@code pathUsername} (not "me") does not exist.
     */
    private Result<User, String> validateEditAccess(
            User currentUser,
            String pathUsername
    ) throws UserNotFoundException {
        // anon /userA/edit -> no
        // anon no request /userA/edit -> no
        if (currentUser == null && !pathUsername.equals("me"))
            return new Result.Err<>("redirect:/profile/" + pathUsername);
        // anon /me/edit -> no
        // anon no request /me/edit -> no
        if (currentUser == null)
            return new Result.Err<>("redirect:/");
        // userB /userA/edit -> no
        // userB no request /userA/edit -> no
        if (!pathUsername.equals("me") && !currentUser.getUsername().equals(pathUsername))
            return new Result.Err<>("redirect:/profile/" + pathUsername);
        // userA /userA/edit -> ok
        // userA no request /userA/edit -> ok
        // userA /me/edit -> ok
        // userB /me/edit -> ok
        // userA no request /me/edit -> ok
        // userB no request /me/edit -> ok

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));

        return new Result.Ok<>(foundUser);
    }

    /**
     * Calls {@link ProfileViewService#validateEditAccess()} to determine if the {@code currentUser} has
     * permission to edit the profile specified by the {@code pathUsername}. also checks if the
     * {@code editRequest} DTO is populated by the form in the previous edit step.
     * </p>
     *
     * @param currentUser  currently authenticated user, or {@code null} if anonymous.
     * @param pathUsername username from the URL path, which can be a specific username or "me".
     * @param editRequest  request body containing profile edit data. May be uninitialized or {@code null}
     *                     if the request is not a form submission.
     * @return A {@code Result.Ok} containing the user to be edited if the request is valid.
     * Returns a {@code Result.Err} with a redirect URL if the request is invalid,
     * the user is unauthorized, or the DTO is uninitialized.
     * @throws UserNotFoundException if a user specified by {@code pathUsername} (not "me") does not exist.
     */
    private Result<User, String> validateEditAccessAndRequest(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) {
        Result<User, String> initialResult = validateEditAccess(currentUser, pathUsername);
        if (initialResult.isErr())
            return initialResult;

        // userA /userA/edit -> ok
        // userA no request /userA/edit -> no
        if (!pathUsername.equals("me") && (editRequest == null || editRequest.isUninitializedForEditing()))
            return new Result.Err<>("redirect:/profile/" + pathUsername + "/edit");
        // userA /me/edit -> ok
        // userB /me/edit -> ok
        // userA no request /me/edit -> no
        // userB no request /me/edit -> no
        if (editRequest == null || editRequest.isUninitializedForEditing())
            return new Result.Err<>("redirect:/profile/me/edit");

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));

        return new Result.Ok<>(foundUser);
    }
}
