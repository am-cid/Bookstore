package com.simple.Bookstore.init;

import com.simple.Bookstore.Role.Role;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${BS_USERNAME:admin}")
    private String adminUsername;

    @Value("${BS_PASSWORD:1234}")
    private String adminPassword;

    public UserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Checking for admin user...");

        // Check if an admin user with the specified username already exists
        Optional<User> existingUser = userRepository.findByUsername(adminUsername);

        if (existingUser.isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(Role.ADMIN);
            adminUser.setDisplayName("am-cid");

            userRepository.save(adminUser);
            log.info("Created new ADMIN user: {}", adminUser.getUsername());
        } else {
            log.info("ADMIN user already exists: {}", existingUser.get().getUsername());
        }
    }
}
