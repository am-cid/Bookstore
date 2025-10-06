package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Book.BookSearchResultDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Comment.CommentProfileViewResponseDTO;
import com.simple.Bookstore.Comment.CommentService;
import com.simple.Bookstore.Exceptions.ForbiddenException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Review.ReviewProfileViewResponseDTO;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserDeleteRequestDTO;
import com.simple.Bookstore.User.UserService;
import com.simple.Bookstore.utils.FormRequest;
import com.simple.Bookstore.utils.ProfileMapper;
import com.simple.Bookstore.utils.Result;
import com.simple.Bookstore.views.SharedModels.ViewThemesModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileViewServiceImpl implements ProfileViewService {
    private final UserService userService;
    private final ThemeService themeService;
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final BookService bookService;

    @Override
    public Pair<ProfileViewModel, ViewThemesModel> buildProfileViewThemes(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UnauthorizedException, UserNotFoundException {
        ProfileViewModel profileViewModel = validateProfileAccess(currentUser, pathUsername);
        Page<ThemeResponseDTO> ownedThemes = themeService.findThemesByUser(profileViewModel.user(), currentUser, pageable);
        ThemeResponseDTO usedTheme = themeService.findUsedTheme(currentUser);
        List<Long> savedThemeIds = themeService
                .findSavedThemes(currentUser)
                .stream()
                .map(ThemeResponseDTO::id)
                .toList();
        return Pair.of(
                profileViewModel,
                new ViewThemesModel(ownedThemes, usedTheme, savedThemeIds)
        );
    }

    @Override
    public Pair<ProfileViewModel, ProfileViewReviewsModel> buildProfileViewReviews(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UnauthorizedException, UserNotFoundException {
        ProfileViewModel profileViewModel = validateProfileAccess(currentUser, pathUsername);
        Page<ReviewProfileViewResponseDTO> reviews = reviewService.findAllReviewsByUser(profileViewModel.user(), pageable);
        return Pair.of(
                profileViewModel,
                new ProfileViewReviewsModel(reviews)
        );
    }

    @Override
    public Pair<ProfileViewModel, ProfileViewCommentsModel> buildProfileViewComments(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws UnauthorizedException, UserNotFoundException {
        ProfileViewModel profileViewModel = validateProfileAccess(currentUser, pathUsername);
        Page<CommentProfileViewResponseDTO> comments = commentService.findAllCommentsByUser(profileViewModel.user(), pageable);
        return Pair.of(
                profileViewModel,
                new ProfileViewCommentsModel(comments)
        );
    }

    @Override
    public Pair<ProfileViewModel, ViewThemesModel> buildProfileViewSavedThemes(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException {
        User foundUser = validateEditDeleteAccess(currentUser, pathUsername);
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        Page<ThemeResponseDTO> savedThemes = themeService.findSavedThemes(foundUser, pageable);
        ThemeResponseDTO usedTheme = themeService.findUsedTheme(foundUser);
        List<Long> savedThemeIds = themeService
                .findSavedThemes(foundUser)
                .stream()
                .map(ThemeResponseDTO::id)
                .toList();
        return Pair.of(
                new ProfileViewModel(foundUser, foundProfile),
                new ViewThemesModel(savedThemes, usedTheme, savedThemeIds)
        );
    }

    @Override
    public Pair<ProfileViewModel, ProfileViewSavedBooksModel> buildProfileViewSavedBooks(
            User currentUser,
            String pathUsername,
            Pageable pageable
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException {
        User foundUser = validateEditDeleteAccess(currentUser, pathUsername);
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        Page<BookSearchResultDTO> savedThemes = bookService.findSavedBooks(foundUser, pageable);
        List<Long> savedBookIds = bookService.findSavedBookIds(foundUser);
        return Pair.of(
                new ProfileViewModel(foundUser, foundProfile),
                new ProfileViewSavedBooksModel(savedThemes, savedBookIds)
        );
    }

    @Override
    public ProfileEditModel buildProfileEditView(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException {
        User foundUser = validateEditDeleteAccess(currentUser, pathUsername);
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        ProfileEditRequestDTO profileEditRequestDTO =
                isUninitialized(editRequest)
                        ? ProfileMapper.profileResponseDtoToFreshEditRequestDTO(foundProfile)
                        : editRequest;
        return new ProfileEditModel(
                foundUser,
                foundProfile,
                profileEditRequestDTO
        );
    }

    @Override
    public ProfileEditModel validateEditAccess(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException {
        User foundUser = validateEditDeleteAccess(currentUser, pathUsername);
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        return new ProfileEditModel(
                foundUser,
                foundProfile,
                editRequest
        );
    }

    @Override
    public Result<ProfileEditModel, String> validateEditAccessAndRequest(
            User currentUser,
            String pathUsername,
            ProfileEditRequestDTO editRequest
    ) throws ForbiddenException, IllegalStateException, UnauthorizedException, UserNotFoundException {
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
    public ProfileDeleteModel validateDeleteAccess(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException {
        User foundUser = validateEditDeleteAccess(currentUser, pathUsername);
        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());
        return new ProfileDeleteModel(
                foundUser,
                foundProfile,
                deleteRequest
        );
    }

    @Override
    public Result<ProfileDeleteModel, String> validateDeleteAccessAndRequest(
            User currentUser,
            String pathUsername,
            UserDeleteRequestDTO deleteRequest
    ) throws ForbiddenException, IllegalStateException, UnauthorizedException, UserNotFoundException {
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
     * determines if the given user can access a profile given the pathUsername
     * <p>
     * This method checks three conditions for access:
     * <ul>
     * <li>user is anonymous and trying to access <code>/me</code></li>
     * <li>target profile is publicly visible.</li>
     * <li>currently authenticated user is the owner of the target profile, public or private.</li>
     * </ul>
     *
     * @param currentUser  currently authenticated user, or {@code null} if anonymous.
     * @param pathUsername username from the path, which can be a specific username or "me".
     * @return A {@link ProfileViewModel} if the user can access the profile. Returns a {@link String}
     * containing a redirect URL string if access is denied.
     */
    private ProfileViewModel validateProfileAccess(
            User currentUser,
            String pathUsername
    ) throws UnauthorizedException, UserNotFoundException {
        if (currentUser == null && pathUsername.equals("me"))
            throw new UnauthorizedException("Need to login to able to access /profile/me");

        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findByUsername(pathUsername)
                .orElseThrow(() -> new UserNotFoundException(pathUsername));

        ProfileResponseDTO foundProfile = ProfileMapper.profileToResponseDTO(foundUser.getProfile());

        if (pathUsername.equals("me")
                || foundProfile.isPublic()
                || currentUser != null && currentUser.getProfile().getId().equals(foundProfile.id())
        )
            return new ProfileViewModel(foundUser, foundProfile);
        else
            throw new UserNotFoundException(pathUsername);
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
     * @if a user specified by {@code pathUsername} (not "me") does not exist.
     */
    private User validateEditDeleteAccess(
            User currentUser,
            String pathUsername
    ) throws ForbiddenException, UnauthorizedException, UserNotFoundException {
        User foundUser = pathUsername.equals("me")
                ? currentUser
                : userService
                .findPublicUserOrSelfByUsername(currentUser, pathUsername);

        // anon /userA/action -> no
        // anon no request /userA/action -> no
        if (currentUser == null && !pathUsername.equals("me"))
            throw new ForbiddenException("You cannot modify %s's profile".formatted(pathUsername));
        // anon /me/action -> no
        // anon no request /me/action -> no
        if (currentUser == null)
            throw new UnauthorizedException("Need to login to able to access /profile/me");
        // userB /userA/action -> no
        // userB no request /userA/action -> no
        if (!pathUsername.equals("me") && !currentUser.getUsername().equals(pathUsername))
            throw new ForbiddenException("You cannot modify %s's profile".formatted(pathUsername));
        // userA /userA/action -> ok
        // userA no request /userA/action -> ok
        // userA /me/action -> ok
        // userB /me/action -> ok
        // userA no request /me/action -> ok
        // userB no request /me/action -> ok

        return foundUser;
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
     * @if a user specified by {@code pathUsername} (not "me") does not exist.
     */
    private Result<User, String> validateEditDeleteAccessAndRequest(
            User currentUser,
            String pathUsername,
            FormRequest formRequest,
            String action
    ) throws IllegalStateException, UnauthorizedException, UserNotFoundException {
        switch (action) {
            case "/edit":
            case "/delete":
                break;
            default:
                throw new IllegalStateException("Invalid action: " + action);
        }

        User foundUser = validateEditDeleteAccess(currentUser, pathUsername);

        // userA /userA/edit -> ok
        // userA /me/edit -> ok
        // userB /me/edit -> ok
        // userA no request /userA/edit -> no
        // userA no request /me/edit -> no
        // userB no request /me/edit -> no
        if (isUninitialized(formRequest))
            return new Result.Err<>("redirect:/profile/" + pathUsername + action);

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
