package com.simple.Bookstore.Theme;

import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/themes")
@RequiredArgsConstructor
public class ThemeController {
    private final ThemeService themeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<ThemeResponseDTO>> getThemes(
            @AuthenticationPrincipal User user,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<ThemeResponseDTO> response = themeService
                .findPublishedOrOwnedUnpublishedThemes(user, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ThemeResponseDTO> createTheme(
            @RequestBody ThemeRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        return new ResponseEntity<>(
                themeService.createTheme(user, request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    public ThemeResponseDTO getTheme(@PathVariable Long id) {
        return themeService.findThemeById(id);
    }

    @PatchMapping("/{id}")
    public ThemeResponseDTO updateTheme(
            @PathVariable Long id,
            @RequestBody ThemeRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        return themeService.updateTheme(id, request, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id, @AuthenticationPrincipal User user) {
        themeService.deleteTheme(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ThemeResponseDTO>> searchThemes(
            @RequestParam(required = false) Optional<String> query,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Long userId = (user != null) ? user.getId() : null;
        Page<ThemeResponseDTO> response = themeService.searchThemes(query, userId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
