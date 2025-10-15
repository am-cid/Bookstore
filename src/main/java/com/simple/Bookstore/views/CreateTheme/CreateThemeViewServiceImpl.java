package com.simple.Bookstore.views.CreateTheme;

import com.simple.Bookstore.Exceptions.ForbiddenException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Theme.ThemeRequestDTO;
import com.simple.Bookstore.Theme.ThemeResponseDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.CssGenerator;
import com.simple.Bookstore.utils.ThemeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateThemeViewServiceImpl implements CreateThemeViewService {
    private final ThemeService themeService;

    @Override
    public String validatePageAccess(
            User user,
            ThemeRequestDTO themeRequestDTO
    ) throws UnauthorizedException {
        if (user == null)
            throw new UnauthorizedException("You need to login to create a theme.");
        ThemeResponseDTO usedTheme = themeService.findUsedTheme(user);
        ThemeRequestDTO toInlineTheme = themeRequestDTO.isEmpty()
                ? usedTheme == null
                ? ThemeRequestDTO.defaultTheme()
                : ThemeMapper.responseToRequestDTO(usedTheme)
                : themeRequestDTO.withCleanHexColors();
        return CssGenerator.toInlineCss(
                List.of(
                        toInlineTheme.base00(),
                        toInlineTheme.base01(),
                        toInlineTheme.base02(),
                        toInlineTheme.base03(),
                        toInlineTheme.base04(),
                        toInlineTheme.base05(),
                        toInlineTheme.base06(),
                        toInlineTheme.base07()
                ),
                "#theme-preview-render"
        );
    }

    @Override
    public String validateCreateRequest(
            User user,
            ThemeRequestDTO themeRequestDTO
    ) throws ForbiddenException, UnauthorizedException {
        String inlineCss = validatePageAccess(user, themeRequestDTO);
        if (themeRequestDTO.isEmpty())
            throw new ForbiddenException("You cannot access this page directly. Go fill out the form first at /create-theme");
        return inlineCss;
    }

    public CreatedThemeViewModel validateCreateRequestThenCreateTheme(
            User user,
            ThemeRequestDTO themeRequestDTO
    ) throws ForbiddenException, UnauthorizedException {
        String inlineCss = validateCreateRequest(user, themeRequestDTO);
        ThemeResponseDTO createdTheme = themeService.createTheme(user, themeRequestDTO.withCleanHexColors());
        ThemeResponseDTO usedTheme = themeService.findUsedTheme(user);
        List<Long> savedThemeIds = themeService.findSavedThemeIds(user);
        return new CreatedThemeViewModel(createdTheme, inlineCss, usedTheme, savedThemeIds);
    }

    @Override
    public CreatedThemeViewModel validateCreateRequestAndReturnViewModel(
            User user,
            ThemeRequestDTO themeRequestDTO,
            Long createdThemeId
    ) throws ForbiddenException, IllegalStateException, UnauthorizedException {
        String inlineCss = validateCreateRequest(user, themeRequestDTO);
        ThemeResponseDTO createdTheme = themeService
                .findPublishedOrOwnedThemeById(user, createdThemeId)
                .orElseThrow(() -> new IllegalStateException(
                        "Theme that's just created should have an ID associated with it. Could not find theme with ID: %d"
                                .formatted(createdThemeId)
                ));
        ThemeResponseDTO usedTheme = themeService.findUsedTheme(user);
        List<Long> savedThemeIds = themeService.findSavedThemeIds(user);
        return new CreatedThemeViewModel(createdTheme, inlineCss, usedTheme, savedThemeIds);
    }
}
