package com.simple.Bookstore.seeders;

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
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;

        log.info("Seeding themes...");
        seedThemesForUser(adminUsername);
        seedThemesForUser("user1");
        seedThemesForUser("user2");
    }

    private void seedThemesForUser(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        // Published theme
        String pubName = username + "-published";
        if (themeRepository.findByName(pubName).isEmpty()) {
            Theme publishedTheme = new Theme();
            publishedTheme.setName(pubName);
            publishedTheme.setDescription("ocean by chriskempson");
            publishedTheme.setProfile(user.getProfile());
            publishedTheme.setPublished(true);
            // THEME: ocean by chriskempson
            // https://github.com/chriskempson/base16-default-schemes/blob/master/ocean.yaml
            publishedTheme.setBase00("2B303B");
            publishedTheme.setBase01("343D46");
            publishedTheme.setBase02("4F5B66");
            publishedTheme.setBase03("65737E");
            publishedTheme.setBase04("A7ADBA");
            publishedTheme.setBase05("C0C5CE");
            publishedTheme.setBase06("DFE1E8");
            publishedTheme.setBase07("EFF1F5");
            themeRepository.save(publishedTheme);
            log.info("Created published theme for {}: {}", username, pubName);
        }

        // Unpublished theme
        String unpubName = username + "-unpublished";
        if (themeRepository.findByName(unpubName).isEmpty()) {
            Theme unpublishedTheme = new Theme();
            unpublishedTheme.setName(unpubName);
            unpublishedTheme.setDescription("cupcake by chriskempson");
            unpublishedTheme.setProfile(user.getProfile());
            unpublishedTheme.setPublished(false);
            // THEME: cupcake by chriskempson
            // https://github.com/chriskempson/base16-default-schemes/blob/master/cupcake.yaml
            unpublishedTheme.setBase00("585062");
            unpublishedTheme.setBase01("72677E");
            unpublishedTheme.setBase02("8b8198");
            unpublishedTheme.setBase03("a59daf");
            unpublishedTheme.setBase04("bfb9c6");
            unpublishedTheme.setBase05("d8d5dd");
            unpublishedTheme.setBase06("f2f1f4");
            unpublishedTheme.setBase07("fbf1f2");
            themeRepository.save(unpublishedTheme);
            log.info("Created unpublished theme for {}: {}", username, unpubName);
        }
    }
}
