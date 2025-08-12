package com.simple.Bookstore.init;

import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Theme.Theme;
import com.simple.Bookstore.Theme.ThemeRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(5)
@RequiredArgsConstructor
public class ThemeSeeder implements CommandLineRunner {
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    @Value("${BS_USERNAME:admin}")
    private String adminUsername;
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void run(String... args) {
        log.info("Seeding themes...");
        seedThemesForUser(adminUsername);
        seedThemesForUser("user1");
        seedThemesForUser("user2");
    }

    private void seedThemesForUser(String username) {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // Published theme
        String pubName = username + "-published";
        if (themeRepository.findByName(pubName).isEmpty()) {
            Theme publishedTheme = new Theme();
            publishedTheme.setName(pubName);
            publishedTheme.setUser(user);
            publishedTheme.setPublished(true);
            publishedTheme.setBase00("1d1f21");
            publishedTheme.setBase01("282a2e");
            publishedTheme.setBase02("373b41");
            publishedTheme.setBase03("969896");
            publishedTheme.setBase04("b4b7b4");
            publishedTheme.setBase05("c5c8c6");
            publishedTheme.setBase06("e0e0e0");
            publishedTheme.setBase07("ffffff");
            themeRepository.save(publishedTheme);
            log.info("Created published theme for {}: {}", username, pubName);
        }

        // Unpublished theme
        String unpubName = username + "-unpublished";
        if (themeRepository.findByName(unpubName).isEmpty()) {
            Theme unpublishedTheme = new Theme();
            unpublishedTheme.setName(unpubName);
            unpublishedTheme.setUser(user);
            unpublishedTheme.setPublished(false);
            unpublishedTheme.setBase00("2e2e2e");
            unpublishedTheme.setBase01("3e3e3e");
            unpublishedTheme.setBase02("4e4e4e");
            unpublishedTheme.setBase03("5e5e5e");
            unpublishedTheme.setBase04("6e6e6e");
            unpublishedTheme.setBase05("7e7e7e");
            unpublishedTheme.setBase06("8e8e8e");
            unpublishedTheme.setBase07("9e9e9e");

            themeRepository.save(unpublishedTheme);
            log.info("Created unpublished theme for {}: {}", username, unpubName);
        }
    }
}
