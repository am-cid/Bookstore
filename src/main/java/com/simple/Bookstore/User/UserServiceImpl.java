package com.simple.Bookstore.User;

import com.simple.Bookstore.Auth.RegisterRequestDTO;
import com.simple.Bookstore.Exceptions.UsernameAlreadyTakenException;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileRepository;
import com.simple.Bookstore.Role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

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
}
