package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Theme.Theme;
import com.simple.Bookstore.Theme.ThemeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
@RequiredArgsConstructor
public class DefaultThemeSeeder implements CommandLineRunner {
    private final ThemeRepository themeRepository;
    @Value("${BS_USERNAME:admin}")
    private String adminUsername;
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting default theme seeder");
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;

        Theme theme = new Theme();
        theme.setName("mangagamer");
        theme.setProfile(null); // not owned by anyone
        theme.setPublished(true);
        // THEME: mangagamer theme ported by am-cid
        // based off the colors of the website https://www.mangagamer.com
        theme.setBase00("A40E60");
        theme.setBase01("FF0000");
        theme.setBase02("F61590");
        theme.setBase03("F88B9E");
        theme.setBase04("FFD376");
        theme.setBase05("AAFFB1");
        theme.setBase06("FFE2E7");
        theme.setBase07("FFF2D3");
        Theme savedTheme = themeRepository.save(theme);
        log.info("Created default theme \"mangagamer\" with id {}", savedTheme.getId());
    }
}
