package com.simple.Bookstore.User;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Exceptions.UsernameAlreadyTakenException;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileRepository;
import com.simple.Bookstore.Role.Role;
import com.simple.Bookstore.Theme.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 30;
    private static final String PASSWORD_REGEX_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,30}$";
    private static final String PASSWORD_LENGTH_ERROR_MESSAGE =
            "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters";
    private static final String PASSWORD_PATTERN_ERROR_MESSAGE = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final ThemeRepository themeRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void createUser(RegisterRequestDTO request, Role role) throws UsernameAlreadyTakenException {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyTakenException(request.username());
        }


        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        User savedUser = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(savedUser);
        profile.setDisplayName(request.displayName() != null ? request.displayName() : request.username());
        profile.setPublic(request.isPublic());
        Profile savedProfile = profileRepository.save(profile);

        savedUser.setProfile(savedProfile);
    }

    @Override
    public User updateUser(
            User user,
            UserUpdateRequestDTO request
    ) throws UsernameAlreadyTakenException {
        user.setUsername(request.username());
        if (!request.newPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.newPassword()));
        }
        User savedUser = userRepository.save(user);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                savedUser,
                savedUser.getPassword(),
                savedUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        return savedUser;
    }


    @Override
    public Optional<String> isValidPasswordLength(String password) {
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            return Optional.of(PASSWORD_PATTERN_ERROR_MESSAGE);
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> isValidPasswordPattern(String password) {
        if (!password.matches(PASSWORD_REGEX_PATTERN)) {
            return Optional.of(PASSWORD_PATTERN_ERROR_MESSAGE);
        }
        return Optional.empty();
    }
}
