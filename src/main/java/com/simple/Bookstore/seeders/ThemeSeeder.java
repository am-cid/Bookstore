package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Theme.ThemeRequestDTO;
import com.simple.Bookstore.Theme.ThemeService;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
@Slf4j(topic = "ThemeSeeder")
@Order(5)
@RequiredArgsConstructor
public class ThemeSeeder implements CommandLineRunner {
    private static final String THEMES_DIRECTORY = "classpath:/static/builtin-themes/";
    private final ThemeService themeService;
    private final UserRepository userRepository;
    @Value("${BS_USERNAME:admin}")
    private String adminUsername;
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void run(String... args) throws IOException {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;

        log.info("Seeding builtin themes...");
        seedBuiltinThemes();

        log.info("Seeding themes...");
        seedThemesForUser(adminUsername);
        seedThemesForUser("user1");
        seedThemesForUser("user2");
    }

    private void seedBuiltinThemes() throws IOException {
        User admin = userRepository
                .findByUsername(adminUsername)
                .orElseThrow(() -> new UserNotFoundException(adminUsername));

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] themeRepos = resolver.getResources(THEMES_DIRECTORY + "*");
        for (Resource themeRepo : themeRepos) {
            if (Files.isDirectory(Paths.get(themeRepo.getURI()))) {
                log.info("Is theme repo. Trying to find themes in {}...", themeRepo.getFilename());
                Resource[] ymlFiles = resolver.getResources(THEMES_DIRECTORY + themeRepo.getFilename() + "/*.y*ml");
                for (Resource ymlFile : ymlFiles) {
                    log.info("Seeding theme {}...", ymlFile.getFilename());
                    ThemeRequestDTO request = themeService.loadThemeFromYaml(
                            ymlFile.getFile(),
                            true,
                            null,
                            null
                    );
                    themeService.createTheme(admin, request);
                }
            } else if (
                    themeRepo.isFile()
                            && themeRepo.getFilename().startsWith("default-")
                            && themeRepo.getFilename().endsWith(".yml") || themeRepo.getFilename().endsWith(".yaml")
            ) {
                log.info("Is default theme file. Seeding default theme {}...", themeRepo.getFilename());
                ThemeRequestDTO request = themeService.loadThemeFromYaml(
                        themeRepo.getFile(),
                        true,
                        "Default Mangagamer Theme",
                        "colors from https://www.mangagamer.com (or you can just unset your theme on the right sidebar to use this)"
                );
                themeService.createTheme(admin, request);
            }
        }
    }

    private void seedThemesForUser(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        List<ThemeRequestDTO> themes = List.of(
                // THEME: ocean by chriskempson
                // https://github.com/chriskempson/base16-default-schemes/blob/master/ocean.yaml
                new ThemeRequestDTO(
                        "ocean-" + username + "-published",
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
                // THEME: cupcake by chriskempson
                // https://github.com/chriskempson/base16-default-schemes/blob/master/cupcake.yaml
                new ThemeRequestDTO(
                        "cupcake-" + username + "-published",
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
                // THEME: default dark by chriskempson
                // https://github.com/chriskempson/base16-default-schemes/blob/master/default-dark.yaml
                new ThemeRequestDTO(
                        "default.dark-" + username + "-published",
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
                // THEME: default light by chriskempson
                // https://github.com/chriskempson/base16-default-schemes/blob/master/default-light.yaml
                new ThemeRequestDTO(
                        "default.light-" + username + "-published",
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
                // THEME: eighties by chriskempson
                // https://github.com/chriskempson/base16-default-schemes/blob/master/eighties.yaml
                new ThemeRequestDTO(
                        "eighties-" + username + "-unpublished",
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
                // THEME: mocha by chriskempson
                // https://github.com/chriskempson/base16-default-schemes/blob/master/mocha.yaml
                new ThemeRequestDTO(
                        "mocha-" + username + "-unpublished",
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
        themes.forEach(theme -> {
            themeService.createTheme(user, theme);
            log.info(
                    "Seeded new {} theme for {}: {}",
                    (theme.published() ? "published" : "unpublished"),
                    user.getUsername(),
                    theme.name()
            );
        });
        log.info("Seeded {} themes", themes.size());
    }
}
