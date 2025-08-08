package com.simple.Bookstore.init;

import com.simple.Bookstore.Role.Role;
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
@Order(1)
public class UserSeeder implements CommandLineRunner {

    private static final String USER1_USERNAME = "user1";
    private static final String USER2_USERNAME = "user2";
    private static final String DEFAULT_PASSWORD = "password";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${BS_USERNAME:admin}")
    private String adminUsername;
    @Value("${BS_PASSWORD:1234}")
    private String adminPassword;
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Checking for admin user...");
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(Role.ADMIN);
            adminUser.setDisplayName("am-cid");

            userRepository.save(adminUser);
            log.info("Created new ADMIN user: {}", adminUser.getUsername());
        } else {
            log.info("ADMIN user already exists");
        }

        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return; // only create sample users in dev

        log.info("Creating two sample users...");
        if (userRepository.findByUsername(USER1_USERNAME).isEmpty()) {
            User user1 = new User();
            user1.setUsername(USER1_USERNAME);
            user1.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user1.setRole(Role.USER);
            user1.setDisplayName("user-one");

            userRepository.save(user1);
            log.info("Created new USER 'user1': {}", user1.getUsername());
        } else {
            log.info("USER already exists: {}", USER2_USERNAME);
        }

        if (userRepository.findByUsername(USER2_USERNAME).isEmpty()) {
            User user2 = new User();
            user2.setUsername(USER2_USERNAME);
            user2.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user2.setRole(Role.USER);
            user2.setDisplayName("user-two");

            userRepository.save(user2);
            log.info("Created new USER 'user2': {}", user2.getUsername());
        } else {
            log.info("USER already exists: {}", USER2_USERNAME);
        }
    }
}
