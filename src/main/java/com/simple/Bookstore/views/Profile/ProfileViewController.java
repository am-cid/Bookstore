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
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) {
        Result<ProfileViewModel, String> viewResult = profileViewService.buildProfileView(
                user,
                pathUsername,
                pageable
        );
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute("pathUsername", pathUsername);
        model.addAttribute("viewModel", viewResult.unwrap());
        return "profile";
    }

    @GetMapping("/{pathUsername}/edit")
    public String profile(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Result<ProfileEditModel, String> viewResult = profileViewService.buildProfileEditView(
                user,
                pathUsername,
                editRequest
        );
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
        Result<ProfileEditModel, String> viewResult = profileViewService.validateProfileEditView(
                user,
                pathUsername,
                editRequest
        );
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        ProfileEditModel viewModel = viewResult.unwrap();
        model.addAttribute("viewModel", viewModel);
        model.addAttribute("profileEditRequestDTO", viewModel.editRequest());
        model.addAttribute("pathUsername", pathUsername);
        model.addAttribute("profileEditRequestDTO", editRequest);

        ///  RESULT ERRORS
        if (result.hasErrors())
            return "profile-edit";
        if (!editRequest.newPassword().equals(editRequest.confirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "error.profileEditRequestDTO", "Passwords do not match.");
            return "profile-edit";
        }
        if (!passwordEncoder.matches(editRequest.oldPassword(), user.getPassword())) {
            result.rejectValue("oldPassword", "error.oldPassword", "Incorrect old password.");
            return "profile-edit";
        }
        if (!user.getUsername().equals(editRequest.username())
                && userService.findByUsername(editRequest.username()).isPresent()) {
            result.rejectValue("oldPassword", "error.oldPassword", "Incorrect old password.");
            return "profile-edit";
        }
        if (!editRequest.newPassword().isEmpty()) {
            Optional<String> lengthErrorMessage = userService.isValidPasswordLength(editRequest.newPassword());
            Optional<String> patternErrorMessage = userService.isValidPasswordPattern(editRequest.newPassword());
            lengthErrorMessage.ifPresent(s -> result.rejectValue("confirmNewPassword", "error.passwordLength", s));
            patternErrorMessage.ifPresent(s -> result.rejectValue("confirmNewPassword", "error.passwordPattern", s));
            if (lengthErrorMessage.isPresent() || patternErrorMessage.isPresent()) {
                return "profile-edit";
            }
        }

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
        Result<ProfileEditRequestDTO, String> viewResult = profileViewService.validateProfileEditConfirmView(user, pathUsername, editRequest);
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute("pathUsername", pathUsername);
        if (viewResult.isErr())
            return viewResult.unwrapErr();

        model.addAttribute("profileEditRequestDTO", viewResult.unwrap());
        return "profile-edit-confirm";
    }

    @PostMapping("/{pathUsername}/edit/confirm")
    public String confirmEditProfile(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        Result<ProfileEditRequestDTO, String> editRequestResult = profileViewService.validateProfileEditConfirmView(user, pathUsername, editRequest);
        if (editRequestResult.isErr())
            return editRequestResult.unwrapErr();

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", editRequestResult.unwrap());
        return "redirect:/profile/{pathUsername}/edit/result";
    }

    @PostMapping(path = "/{pathUsername}/edit/confirm", params = {"_action=back"})
    public String confirmEditProfileGoBack(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        Result<ProfileEditRequestDTO, String> editRequestResult = profileViewService.validateProfileEditConfirmView(user, pathUsername, editRequest);
        if (editRequestResult.isErr())
            return editRequestResult.unwrapErr();

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", editRequestResult.unwrap());
        return "redirect:/profile/{pathUsername}/edit";
    }

    @GetMapping("/{pathUsername}/edit/result")
    public String editResult(
            @PathVariable String pathUsername,
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO editRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Result<ProfileEditModel, String> editRequestResult = profileViewService.buildProfileEditResultView(user, pathUsername, editRequest);
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

    @GetMapping("/me/delete")
    public String deleteProfile(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (user == null) {
            // TODO: error page, need authenticated
            return "redirect:/";
        }

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute("userDeleteRequestDTO", new UserDeleteRequestDTO(null));
        return "profile-delete";
    }

    @PostMapping("/me/delete")
    public String deleteProfile(
            @ModelAttribute("userDeleteRequestDTO") UserDeleteRequestDTO deleteRequest,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (deleteRequest == null || deleteRequest.password().isBlank())
            return "redirect:/profile/me/delete";

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        if (result.hasErrors()) {
            return "profile-delete";
        } else if (!passwordEncoder.matches(deleteRequest.password(), user.getPassword())) {
            result.rejectValue("password", "error.userDeleteRequestDTO", "Incorrect password.");
            return "profile-delete";
        }
        redirectAttributes.addFlashAttribute("userDeleteRequestDTO", deleteRequest);
        return "redirect:/profile/me/delete/result";
    }

    @GetMapping("/me/delete/result")
    public String deleteProfileResult(
            @ModelAttribute("userDeleteRequestDTO") UserDeleteRequestDTO deleteRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) throws ServletException {
        if (deleteRequest == null || deleteRequest.password().isBlank())
            return "redirect:/profile/me/delete";

        userService.deleteUser(user);
        request.logout();

        HeaderAndSidebarsModelAttributes.defaults(null, model, bookService, reviewService, themeService);
        return "profile-delete-result";
    }
}
