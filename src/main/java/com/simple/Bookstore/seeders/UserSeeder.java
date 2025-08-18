package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileRepository;
import com.simple.Bookstore.Role.Role;
import com.simple.Bookstore.Theme.Theme;
import com.simple.Bookstore.Theme.ThemeRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(2)
public class UserSeeder implements CommandLineRunner {

    private static final String USER1_USERNAME = "user1";
    private static final String USER2_USERNAME = "user2";
    private static final String DEFAULT_PASSWORD = "password";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final ThemeRepository themeRepository;
    @Value("${BS_USERNAME:admin}")
    private String adminUsername;
    @Value("${BS_PASSWORD:1234}")
    private String adminPassword;
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public UserSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ProfileRepository profileRepository,
            ThemeRepository themeRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
        this.themeRepository = themeRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Checking for admin user...");
        createUser(adminUsername, adminPassword, Role.ADMIN, true);

        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return; // only create sample users in dev

        log.info("Creating two sample users...");
        createUser(USER1_USERNAME, DEFAULT_PASSWORD, Role.USER, true);
        createUser(USER2_USERNAME, adminPassword, Role.USER, false);
    }

    private void createUser(String username, String password, Role role, boolean isPublic) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            User savedUser = userRepository.save(user);

            Profile profile = new Profile();
            profile.setUser(savedUser);
            profile.setDisplayName(username);
            profile.setPublic(isPublic);
            Theme defaultTheme = themeRepository
                    .findById(1L)
                    .orElseThrow(() -> new IllegalStateException("Default theme not found"));
            profile.setThemeUsed(defaultTheme);
            Profile savedProfile = profileRepository.save(profile);

            savedUser.setProfile(savedProfile);
            User newlySaved = userRepository.save(savedUser);
            log.info("Created new user (with role {}): {}", role.toString(), savedUser.getUsername());
        } else {
            log.info("{} already exists: {}", role.toString(), username);
        }
    }
}
