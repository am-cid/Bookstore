package com.simple.Bookstore.Profile;

import com.simple.Bookstore.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponseDTO> getProfile(
            @PathVariable String username,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(profileService.findByUsername(username, user));
    }

    @PostMapping("/theme")
    public ResponseEntity<ProfileResponseDTO> setTheme(
            @RequestParam("themeId") Long themeId,
            @AuthenticationPrincipal User user
    ) {
        ProfileResponseDTO response = profileService.setTheme(themeId, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/theme", params = {"_method=delete"})
    public ResponseEntity<ProfileResponseDTO> unsetThemeAndUseDefaultTheme(
            @AuthenticationPrincipal User user
    ) {
        profileService.unsetThemeAndUseDefaultTheme(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProfileResponseDTO>> searchProfile(
            @RequestParam(required = false) String query,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(profileService.searchProfiles(query, user, pageable));
    }
}
