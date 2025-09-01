package com.simple.Bookstore.views;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Profile.ProfileEditRequestDTO;
import com.simple.Bookstore.Profile.ProfileResponseDTO;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserService;
import com.simple.Bookstore.User.UserUpdateRequestDTO;
import com.simple.Bookstore.utils.ProfileMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @GetMapping("/me")
    public String profile(
            @AuthenticationPrincipal User user,
            @PageableDefault Pageable pageable,
            Model model
    ) throws UnauthorizedException {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        ProfileResponseDTO profile = profileService.findOwn(user);
        model.addAttribute("profile", profile);

        Page<ThemeResponseDTO> createdThemes = themeService.findThemesByUser(user, pageable);
        model.addAttribute("ownedThemes", createdThemes);
        model.addAttribute(
                "usedTheme",
                themeService.findUsedTheme(user)
        );
        model.addAttribute(
                "savedThemeIds",
                themeService
                        .findSavedThemes(user)
                        .stream()
                        .map(ThemeResponseDTO::id)
                        .toList()
        );
        return "profile";
    }

    @GetMapping("/me/edit")
    public String editProfile(
            @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO request,
            @AuthenticationPrincipal User user,
            Model model
    ) throws UnauthorizedException {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        ProfileResponseDTO profile = profileService.findOwn(user);
        model.addAttribute("profile", profile);

        model.addAttribute(
                "profileEditRequestDTO",
                request.isUninitializedForEditing()
                        ? ProfileMapper.profileToFreshEditRequestDTO(user.getProfile())
                        : request
        );
        return "profile-edit";
    }

    @PostMapping("/me/edit")
    public String editProfile(
            @Valid @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO request,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model,
            RedirectAttributes redirectAttributes
    ) throws UnauthorizedException {
        if (request == null) {
            return "redirect:/profile/me/edit";
        }

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        ProfileResponseDTO profile = profileService.findOwn(user);
        model.addAttribute("profile", profile);

        if (result.hasErrors()) {
            return "profile-edit";
        } else if (!request.newPassword().equals(request.confirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "error.profileEditRequestDTO", "Passwords do not match.");
            return "profile-edit";
        } else if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            result.rejectValue("oldPassword", "error.oldPassword", "Incorrect old password.");
            return "profile-edit";
        } else if (
                !user.getUsername().equals(request.username())
                        && userService.findByUsername(request.username()).isPresent()
        ) {
            result.rejectValue("username", "error.usernameExists", "Username already exists.");
            return "profile-edit";
        } else if (!request.newPassword().isEmpty()) {
            Optional<String> lengthErrorMessage = userService.isValidPasswordLength(request.newPassword());
            Optional<String> patternErrorMessage = userService.isValidPasswordPattern(request.newPassword());
            lengthErrorMessage.ifPresent(s -> result.rejectValue("newPassword", "error.passwordLength", s));
            patternErrorMessage.ifPresent(s -> result.rejectValue("newPassword", "error.passwordPattern", s));
            if (lengthErrorMessage.isPresent() || patternErrorMessage.isPresent()) {
                return "profile-edit";
            }
        }

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", request);
        return "redirect:/profile/me/edit/confirm";
    }

    @GetMapping("/me/edit/confirm")
    public String confirmEditProfile(
            @Valid @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO request,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (request == null) {
            return "redirect:/profile/me/edit";
        }

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        return "profile-edit-confirm";
    }

    @PostMapping("/me/edit/confirm")
    public String confirmEditProfile(
            @Valid @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO request,
            RedirectAttributes redirectAttributes
    ) {
        if (request == null) {
            return "redirect:/profile/me/edit";
        }

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", request);
        return "redirect:/profile/me/edit/result";
    }

    @PostMapping(path = "/me/edit/confirm", params = {"_action=back"})
    public String confirmEditProfileGoBack(
            @Valid @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO request,
            RedirectAttributes redirectAttributes
    ) {
        if (request == null) {
            return "redirect:/profile/me/edit";
        }

        redirectAttributes.addFlashAttribute("profileEditRequestDTO", request);
        return "redirect:/profile/me/edit";
    }

    @GetMapping("/me/edit/result")
    public String editResult(
            @Valid @ModelAttribute("profileEditRequestDTO") ProfileEditRequestDTO request,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (request == null) {
            return "redirect:/profile/me/edit";
        }

        User savedUser = userService.updateUser(user, new UserUpdateRequestDTO(
                request.username(),
                request.newPassword()
        ));
        profileService.updateProfile(savedUser.getProfile(), request);
        HeaderAndSidebarsModelAttributes.defaults(savedUser, model, bookService, reviewService, themeService);
        return "profile-edit-result";
    }
}
