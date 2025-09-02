package com.simple.Bookstore.User;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Exceptions.UsernameAlreadyTakenException;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileRepository;
import com.simple.Bookstore.Role.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
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
    private static final String PASSWORD_REGEX_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{0,30}$";
    private static final String PASSWORD_LENGTH_ERROR_MESSAGE =
            "Password must be between " + PASSWORD_MIN_LENGTH + " and " + PASSWORD_MAX_LENGTH + " characters";
    private static final String PASSWORD_PATTERN_ERROR_MESSAGE = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createUser(RegisterRequestDTO request, Role role) throws UsernameAlreadyTakenException {
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
        profile.setDisplayName(
                request.displayName() == null || request.displayName().isBlank()
                        ? request.username()
                        : request.displayName());
        profile.setPublic(request.isPublic());
        Profile savedProfile = profileRepository.save(profile);

        savedUser.setProfile(savedProfile);
        return userRepository.save(savedUser);
    }

    @Override
    public User updateAuthenticatedUser(
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
                request.newPassword(),
                savedUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        return savedUser;
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public Optional<String> isValidPasswordLength(String password) {
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            return Optional.of(PASSWORD_LENGTH_ERROR_MESSAGE);
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
