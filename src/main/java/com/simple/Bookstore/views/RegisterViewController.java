package com.simple.Bookstore.views;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Role.Role;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterViewController {
    private final BookService bookService;
    private final ThemeService themeService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final HttpServletRequest request;

    @GetMapping
    public String register(
            @ModelAttribute("registerRequestDTO") RegisterRequestDTO registerRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute(
                "registerRequestDTO",
                registerRequest == null || registerRequest.isEmpty()
                        ? RegisterRequestDTO.empty()
                        : registerRequest
        );
        model.addAttribute(
                "usedCustomDisplayName",
                registerRequest != null
                        && !registerRequest.isEmpty()
                        && !registerRequest.username().equals(registerRequest.displayName())
        );
        return "register";
    }

    @PostMapping
    public String register(
            @Valid @ModelAttribute("registerRequestDTO") RegisterRequestDTO registerRequest,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        boolean usedCustomDisplayName = !registerRequest.username().equals(registerRequest.displayName());
        model.addAttribute("usedCustomDisplayName", usedCustomDisplayName);
        if (result.hasErrors()) {
            return "register";
        } else if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerRequestDTO", "Passwords do not match.");
            return "register";
        } else if (userService.findByUsername(registerRequest.username()).isPresent()) {
            result.rejectValue("username", "error.registerRequestDTO", "Username is already in use.");
            return "register";
        }
        redirectAttributes.addFlashAttribute("registerRequestDTO", registerRequest);
        redirectAttributes.addFlashAttribute("usedCustomDisplayName", usedCustomDisplayName);
        return "redirect:/register/confirm";
    }

    @GetMapping("/confirm")
    public String confirmRegistration(
            @ModelAttribute("registerRequestDTO") RegisterRequestDTO registerRequest,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        if (registerRequest == null || registerRequest.isEmpty())
            return "redirect:/register";

        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        return "register-confirm";
    }

    @PostMapping("/confirm")
    public String confirmRegistration(
            @ModelAttribute("registerRequestDTO") RegisterRequestDTO registerRequest,
            RedirectAttributes redirectAttributes
    ) {
        if (registerRequest == null || registerRequest.isEmpty())
            return "redirect:/register";

        redirectAttributes.addFlashAttribute("registerRequestDTO", registerRequest);
        redirectAttributes.addFlashAttribute("usedCustomDisplayName", !registerRequest.username().equals(registerRequest.displayName()));
        return "redirect:/register/result";
    }

    @PostMapping(path = "/confirm", params = {"_action=back"})
    public String confirmRegistrationGoBack(
            @ModelAttribute("registerRequestDTO") RegisterRequestDTO registerRequest,
            RedirectAttributes redirectAttributes
    ) {
        if (registerRequest == null || registerRequest.isEmpty())
            return "redirect:/register";

        redirectAttributes.addFlashAttribute("registerRequestDTO", registerRequest);
        redirectAttributes.addFlashAttribute("usedCustomDisplayName", !registerRequest.username().equals(registerRequest.displayName()));
        return "redirect:/register";
    }

    @GetMapping("/result")
    public String registrationResult(
            @ModelAttribute("registerRequestDTO") RegisterRequestDTO registerRequest,
            Model model
    ) throws ServletException {
        if (registerRequest == null || registerRequest.isEmpty())
            return "redirect:/register";

        User newUser = userService.createUser(registerRequest, Role.USER);
        request.login(registerRequest.username(), registerRequest.password());
        HeaderAndSidebarsModelAttributes.defaults(newUser, model, bookService, reviewService, themeService);
        return "register-result";
    }
}
