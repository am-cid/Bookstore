package com.simple.Bookstore.views.CreateTheme;

import com.simple.Bookstore.Book.BookService;
import com.simple.Bookstore.Profile.ProfileService;
import com.simple.Bookstore.Review.ReviewService;
import com.simple.Bookstore.Theme.ThemeRequestDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.views.HeaderAndSidebarsModelAttributes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/create-theme")
public class CreateThemeViewController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final ThemeService themeService;
    private final ProfileService profileService;
    private final CreateThemeViewService createThemeViewService;

    @GetMapping
    public String createTheme(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        String previewThemeCss = createThemeViewService.validatePageAccess(user, themeRequestDTO);
        model.addAttribute("previewThemeCss", previewThemeCss);
        return "create-theme";
    }

    @PostMapping
    public String validateThemeRequest(
            @Valid @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @AuthenticationPrincipal User user,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        createThemeViewService.validateCreateRequest(user, themeRequestDTO);
        redirectAttributes.addFlashAttribute("themeRequestDTO", themeRequestDTO);

        if (result.hasErrors())
            return "redirect:/create-theme";
        return "redirect:/create-theme/confirm";
    }

    @GetMapping("/confirm")
    public String createThemeConfirm(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);

        String previewThemeCss = createThemeViewService.validateCreateRequest(user, themeRequestDTO);
        model.addAttribute("previewThemeCss", previewThemeCss);
        return "create-theme-confirm";
    }

    @PostMapping("/confirm")
    public String createThemeConfirmProceed(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        CreatedThemeViewModel viewModel = createThemeViewService
                .validateCreateRequestThenCreateTheme(user, themeRequestDTO);

        redirectAttributes.addFlashAttribute("viewModel", viewModel);
        redirectAttributes.addFlashAttribute("themeRequestDTO", themeRequestDTO);
        return "redirect:/create-theme/result";
    }

    @PostMapping(value = "/confirm", params = {"_action=back"})
    public String createThemeConfirmGoBack(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        String previewThemeCss = createThemeViewService.validateCreateRequest(user, themeRequestDTO);
        redirectAttributes.addFlashAttribute("themeRequestDTO", themeRequestDTO);
        return "redirect:/create-theme";
    }

    @GetMapping("/result")
    public String createThemeResult(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @ModelAttribute("viewModel") CreatedThemeViewModel viewModel,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        HeaderAndSidebarsModelAttributes.defaults(user, model, bookService, reviewService, themeService);
        createThemeViewService.validateCreateRequest(user, themeRequestDTO);
        return "create-theme-result";
    }

    @PostMapping("/result/apply")
    public RedirectView applyCreatedTheme(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @RequestParam("themeId") Long themeId,
            @AuthenticationPrincipal User user,
            @RequestHeader("Referer") String referer,
            RedirectAttributes redirectAttributes
    ) {
        profileService.setTheme(themeId, user);
        CreatedThemeViewModel viewModel = createThemeViewService.validateCreateRequestAndReturnViewModel(user, themeRequestDTO, themeId);
        redirectAttributes.addFlashAttribute("viewModel", viewModel);
        redirectAttributes.addFlashAttribute("themeRequestDTO", themeRequestDTO);
        return new RedirectView(referer);
    }

    @PostMapping("/result/save")
    public RedirectView saveCreatedTheme(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @RequestParam("themeId") Long themeId,
            @AuthenticationPrincipal User user,
            @RequestHeader("Referer") String referer,
            RedirectAttributes redirectAttributes
    ) {
        profileService.saveTheme(themeId, user);
        CreatedThemeViewModel viewModel = createThemeViewService.validateCreateRequestAndReturnViewModel(user, themeRequestDTO, themeId);
        redirectAttributes.addFlashAttribute("viewModel", viewModel);
        redirectAttributes.addFlashAttribute("themeRequestDTO", themeRequestDTO);
        return new RedirectView(referer);
    }

    @PostMapping("/result/unsave")
    public RedirectView unsaveCreatedTheme(
            @ModelAttribute("themeRequestDTO") ThemeRequestDTO themeRequestDTO,
            @RequestParam("themeId") Long themeId,
            @AuthenticationPrincipal User user,
            @RequestHeader("Referer") String referer,
            RedirectAttributes redirectAttributes
    ) {
        profileService.unsaveTheme(themeId, user);
        CreatedThemeViewModel viewModel = createThemeViewService.validateCreateRequestAndReturnViewModel(user, themeRequestDTO, themeId);
        redirectAttributes.addFlashAttribute("viewModel", viewModel);
        redirectAttributes.addFlashAttribute("themeRequestDTO", themeRequestDTO);
        return new RedirectView(referer);
    }
}
