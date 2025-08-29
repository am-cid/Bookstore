package com.simple.Bookstore.views;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Role.Role;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegisterViewController {
    private final BookService bookService;
    private final ThemeService themeService;
    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/register")
    public String register(
            @AuthenticationPrincipal User user,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        model.addAttribute("registerRequestDTO", RegisterRequestDTO.empty());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequestDTO") RegisterRequestDTO request,
            BindingResult result,
            @AuthenticationPrincipal User user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        if (result.hasErrors()) {
            return "register";
        } else if (!request.password().equals(request.confirmPassword())) {
            result.rejectValue("confirmPassword", "error.registerRequestDTO", "Passwords do not match.");
            HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
            return "register";
        }
        userService.createUser(request, Role.USER);
        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully.");
        return "redirect:/";
    }
}
