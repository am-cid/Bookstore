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

import java.util.List;

@Component
@Slf4j(topic = "ThemeSeeder")
@Order(5)
@RequiredArgsConstructor
public class ThemeSeeder implements CommandLineRunner {
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    @Value("${BS_USERNAME:admin}")
    private String adminUsername;
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private static Theme createTheme(
            String pubName,
            User user,
            String description,
            boolean published,
            String base00,
            String base01,
            String base02,
            String base03,
            String base04,
            String base05,
            String base06,
            String base07
    ) {
        Theme publishedTheme = new Theme();
        publishedTheme.setName(pubName);
        publishedTheme.setDescription(description);
        publishedTheme.setProfile(user.getProfile());
        publishedTheme.setPublished(published);
        publishedTheme.setBase00(base00);
        publishedTheme.setBase01(base01);
        publishedTheme.setBase02(base02);
        publishedTheme.setBase03(base03);
        publishedTheme.setBase04(base04);
        publishedTheme.setBase05(base05);
        publishedTheme.setBase06(base06);
        publishedTheme.setBase07(base07);

        log.info(
                "Seeded new {} theme for {}: {}",
                (published ? "published" : "unpublished"),
                user.getUsername(),
                pubName
        );
        return publishedTheme;
    }

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
        List<Theme> themes = List.of(
                createTheme(
                        // THEME: ocean by chriskempson
                        // https://github.com/chriskempson/base16-default-schemes/blob/master/ocean.yaml
                        "ocean-" + username + "-published", user,
                        "ocean by chriskempson", true,
                        "2B303B",
                        "343D46",
                        "4F5B66",
                        "65737E",
                        "A7ADBA",
                        "C0C5CE",
                        "DFE1E8",
                        "EFF1F5"
                ),
                createTheme(
                        // THEME: cupcake by chriskempson
                        // https://github.com/chriskempson/base16-default-schemes/blob/master/cupcake.yaml
                        "cupcake-" + username + "-published", user,
                        "cupcake by chriskempson", true,
                        "585062",
                        "72677E",
                        "8b8198",
                        "a59daf",
                        "bfb9c6",
                        "d8d5dd",
                        "f2f1f4",
                        "fbf1f2"
                ),
                createTheme(
                        // THEME: default dark by chriskempson
                        // https://github.com/chriskempson/base16-default-schemes/blob/master/default-dark.yaml
                        "default.dark-" + username + "-published", user,
                        "default dark by chriskempson", true,
                        "181818",
                        "282828",
                        "383838",
                        "585858",
                        "b8b8b8",
                        "d8d8d8",
                        "e8e8e8",
                        "f8f8f8"
                ),
                createTheme(
                        // THEME: default light by chriskempson
                        // https://github.com/chriskempson/base16-default-schemes/blob/master/default-light.yaml
                        "default.light-" + username + "-published", user,
                        "default light by chriskempson", true,
                        "f8f8f8",
                        "e8e8e8",
                        "d8d8d8",
                        "b8b8b8",
                        "585858",
                        "383838",
                        "282828",
                        "181818"
                ),
                createTheme(
                        // THEME: eighties by chriskempson
                        // https://github.com/chriskempson/base16-default-schemes/blob/master/eighties.yaml
                        "eighties-" + username + "-unpublished", user,
                        "eighties by chriskempson", false,
                        "2d2d2d",
                        "393939",
                        "515151",
                        "747369",
                        "a09f93",
                        "d3d0c8",
                        "e8e6df",
                        "f2f0ec"
                ),
                createTheme(
                        // THEME: mocha by chriskempson
                        // https://github.com/chriskempson/base16-default-schemes/blob/master/mocha.yaml
                        "mocha-" + username + "-unpublished", user,
                        "mocha by chriskempson", false,
                        "3B3228",
                        "534636",
                        "645240",
                        "7e705a",
                        "b8afad",
                        "d0c8c6",
                        "e9e1dd",
                        "f5eeeb"
                )
        );
        log.info("Seeded {} themes", themes.size());
        themeRepository.saveAll(themes);
    }
}
