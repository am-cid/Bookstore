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
            // THEME: cupcake by chriskempson
            // https://github.com/chriskempson/base16-default-schemes/blob/master/cupcake.yaml
            publishedTheme.setBase00("585062");
            publishedTheme.setBase01("72677E");
            publishedTheme.setBase02("8b8198");
            publishedTheme.setBase03("a59daf");
            publishedTheme.setBase04("bfb9c6");
            publishedTheme.setBase05("d8d5dd");
            publishedTheme.setBase06("f2f1f4");
            publishedTheme.setBase07("fbf1f2");
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
            // THEME: mangagamer theme by am-cid
            // based off the colors of the website https://www.mangagamer.com
            unpublishedTheme.setBase00("A40E60");
            unpublishedTheme.setBase01("FF0000");
            unpublishedTheme.setBase02("F61590");
            unpublishedTheme.setBase03("F88B9E");
            unpublishedTheme.setBase04("FFD376");
            unpublishedTheme.setBase05("AAFFB1");
            unpublishedTheme.setBase06("FFE2E7");
            unpublishedTheme.setBase07("FFF2D3");
            themeRepository.save(unpublishedTheme);
            log.info("Created unpublished theme for {}: {}", username, unpubName);
        }
    }
}
