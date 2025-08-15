package com.simple.Bookstore.Profile;

import com.simple.Bookstore.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
