package com.simple.Bookstore.views.Profile;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserDeleteRequestDTO;
import com.simple.Bookstore.User.UserService;
import com.simple.Bookstore.User.UserUpdateRequestDTO;
import com.simple.Bookstore.utils.Result;
import com.simple.Bookstore.views.HeaderAndSidebarsModelAttributes;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileViewController {
    private final UserService userService;
    private final ProfileService profileService;
    private final BookService bookService;
    private final ThemeService themeService;
    private final ReviewService reviewService;
    private final PasswordEncoder passwordEncoder;
    private final HttpServletRequest request;
    private final ProfileViewService profileViewService;

    @GetMapping("/{pathUsername}")
    public String profile(
            @PathVariable String pathUsername,
            @RequestParam(required = false) ProfileViewType view,
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) {
        ProfileViewType actualView = view == null ? ProfileViewType.THEMES : view;
        model.addAttribute("viewType", actualView);
        switch (actualView) {
            case THEMES:
                Result<Pair<ProfileViewModel, ProfileViewThemesModel>, String>
                        pairThemesResult = profileViewService
                        .buildProfileViewThemes(user, pathUsername, pageable);
                if (pairThemesResult.isErr())
                    return pairThemesResult.unwrapErr();
                Pair<ProfileViewModel, ProfileViewThemesModel>
                        pairThemes = pairThemesResult.unwrap();
                model.addAttribute("viewModel", pairThemes.getFirst());
                model.addAttribute("viewThemesModel", pairThemes.getSecond());
                break;
            case REVIEWS:
                Result<Pair<ProfileViewModel, ProfileViewReviewsModel>, String>
                        pairReviewResult = profileViewService
                        .buildProfileViewReviews(user, pathUsername, pageable);
                if (pairReviewResult.isErr())
                    return pairReviewResult.unwrapErr();
                Pair<ProfileViewModel, ProfileViewReviewsModel>
                        pairReviews = pairReviewResult.unwrap();
                model.addAttribute("viewModel", pairReviews.getFirst());
                model.addAttribute("viewReviewsModel", pairReviews.getSecond());
                break;
            case COMMENTS:
                Result<Pair<ProfileViewModel, ProfileViewCommentsModel>, String>
                        pairCommentsResult = profileViewService
                        .buildProfileViewComments(user, pathUsername, pageable);
                if (pairCommentsResult.isErr())
                    return pairCommentsResult.unwrapErr();
                Pair<ProfileViewModel, ProfileViewCommentsModel>
                        pairComments = pairCommentsResult.unwrap();
                model.addAttribute("viewModel", pairComments.getFirst());
                model.addAttribute("viewCommentsModel", pairComments.getSecond());
                break;
        }

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute("pathUsername", pathUsername);
        return "profile";
    }

    @GetMapping("/{pathUsername}/edit")
    public String profile(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Result<ProfileEditModel, String> viewResult = profileViewService
                .buildProfileEditView(user, pathUsername, editRequest);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        ProfileEditModel viewModel = viewResult.unwrap();
        model.addAttribute("pathUsername", pathUsername);
        model.addAttribute("viewModel", viewModel);
        model.addAttribute("profileEditRequestDTO", viewModel.editRequest());
        return "profile-edit";
    }

    @PostMapping("/{pathUsername}/edit")
    public String editProfile(
            @PathVariable String pathUsername,
            @Valid @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws UnauthorizedException {
        Result<ProfileEditModel, String> viewResult = profileViewService
                .validateEditAccess(user, pathUsername, editRequest);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        ProfileEditModel viewModel = viewResult.unwrap();
        model.addAttribute("viewModel", viewModel);
        model.addAttribute("pathUsername", pathUsername);
        model.addAttribute("profileEditRequestDTO", viewModel.editRequest());

        ///  RESULT ERRORS
        boolean hasErrors = result.hasErrors();
        if (!editRequest.newPassword().equals(editRequest.confirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "error.profileEditRequestDTO", "Passwords do not match.");
            hasErrors = true;
        }
        if (!passwordEncoder.matches(editRequest.oldPassword(), user.getPassword())) {
            result.rejectValue("oldPassword", "error.oldPassword", "Incorrect old password.");
            hasErrors = true;
        }
        if (!user.getUsername().equals(editRequest.username())
                && userService.findByUsername(editRequest.username()).isPresent()) {
            result.rejectValue("oldPassword", "error.oldPassword", "Incorrect old password.");
            hasErrors = true;
        }
        if (!editRequest.newPassword().isEmpty()) {
            Optional<String> lengthErrorMessage = userService.isValidPasswordLength(editRequest.newPassword());
            Optional<String> patternErrorMessage = userService.isValidPasswordPattern(editRequest.newPassword());
            lengthErrorMessage.ifPresent(s -> result.rejectValue("confirmNewPassword", "error.passwordLength", s));
            patternErrorMessage.ifPresent(s -> result.rejectValue("confirmNewPassword", "error.passwordPattern", s));
            if (lengthErrorMessage.isPresent() || patternErrorMessage.isPresent()) {
                hasErrors = true;
            }
        }
        if (hasErrors)
            return "profile-edit";

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", viewResult.unwrap().editRequest());
        return "redirect:/profile/{pathUsername}/edit/confirm";
    }

    @GetMapping("/{pathUsername}/edit/confirm")
    public String confirmEditProfile(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Result<ProfileEditModel, String> viewResult = profileViewService
                .validateEditAccessAndRequest(user, pathUsername, editRequest);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute("pathUsername", pathUsername);
        model.addAttribute("profileEditRequestDTO", viewResult.unwrap().editRequest());
        return "profile-edit-confirm";
    }

    @PostMapping("/{pathUsername}/edit/confirm")
    public String confirmEditProfile(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        Result<ProfileEditModel, String> viewResult = profileViewService
                .validateEditAccessAndRequest(user, pathUsername, editRequest);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", viewResult.unwrap().editRequest());
        return "redirect:/profile/{pathUsername}/edit/result";
    }

    @PostMapping(path = "/{pathUsername}/edit/confirm", params = {"_action=back"})
    public String confirmEditProfileGoBack(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        Result<ProfileEditModel, String> viewResult = profileViewService
                .validateEditAccessAndRequest(user, pathUsername, editRequest);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", viewResult.unwrap().editRequest());
        return "redirect:/profile/{pathUsername}/edit";
    }

    @GetMapping("/{pathUsername}/edit/result")
    public String editResult(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Result<ProfileEditModel, String> editRequestResult = profileViewService
                .validateEditAccessAndRequest(user, pathUsername, editRequest);
        if (editRequestResult.isErr())
            return editRequestResult.unwrapErr();

        ProfileEditModel viewModel = editRequestResult.unwrap();
        ProfileEditRequestDTO editRequestDTO = viewModel.editRequest();
        User savedUser = userService.updateAuthenticatedUser(
                viewModel.user(),
                new UserUpdateRequestDTO(
                        editRequestDTO.username(),
                        editRequestDTO.newPassword()
                )
        );
        profileService.updateProfile(savedUser.getProfile(), editRequestDTO);
        HeaderAndSidebarsModelAttributes.defaults(savedUser, model, bookService, reviewService, themeService);
        return "profile-edit-result";
    }

    @GetMapping("/{pathUsername}/delete")
    public String deleteProfile(
            @PathVariable String pathUsername,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Result<ProfileDeleteModel, String> viewResult = profileViewService
                .validateDeleteAccess(user, pathUsername, UserDeleteRequestDTO.empty());
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        ProfileDeleteModel viewModel = viewResult.unwrap();
        model.addAttribute("pathUsername", pathUsername);
        model.addAttribute("viewModel", viewModel);
        model.addAttribute("userDeleteRequestDTO", viewModel.deleteRequest());
        return "profile-delete";
    }

    @PostMapping("/{pathUsername}/delete")
    public String deleteProfile(
            @PathVariable String pathUsername,
            @ModelAttribute("userDeleteRequestDTO") UserDeleteRequestDTO deleteRequest,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Result<ProfileDeleteModel, String> viewResult = profileViewService
                .validateDeleteAccessAndRequest(user, pathUsername, deleteRequest);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        if (result.hasErrors()) {
            return "profile-delete";
        } else if (!passwordEncoder.matches(deleteRequest.password(), user.getPassword())) {
            result.rejectValue("password", "error.userDeleteRequestDTO", "Incorrect password.");
            return "profile-delete";
        }

        redirectAttributes.addFlashAttribute("userDeleteRequestDTO", viewResult.unwrap().deleteRequest());
        return "redirect:/profile/me/delete/result";
    }

    @GetMapping("/{pathUsername}/delete/result")
    public String deleteProfileResult(
            @PathVariable String pathUsername,
            @ModelAttribute("userDeleteRequestDTO") UserDeleteRequestDTO deleteRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) throws ServletException {
        Result<ProfileDeleteModel, String> viewResult = profileViewService
                .validateDeleteAccessAndRequest(user, pathUsername, deleteRequest);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        userService.deleteUser(user);
        request.logout();

        HeaderAndSidebarsModelAttributes.defaults(null, model, bookService, reviewService, themeService);
        return "profile-delete-result";
    }
}
