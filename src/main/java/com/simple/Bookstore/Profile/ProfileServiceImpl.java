package com.simple.Bookstore.Profile;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Theme.Theme;
import com.simple.Bookstore.Theme.ThemeRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final ThemeRepository themeRepository;

    @Override
    public ProfileResponseDTO findByUsername(String username, User user) {
        if (user != null && user.getUsername().equals(username)) {
            return profileRepository
                    .findByUserUsername(username)
                    .map(ProfileMapper::profileToResponseDTO)
                    .orElseThrow(() -> new UserNotFoundException(username));
        } else {
            return profileRepository
                    .findByUserUsernameAndIsPublicIsTrue(username)
                    .map(ProfileMapper::profileToResponseDTO)
                    .orElseThrow(() -> new UserNotFoundException(username));
        }
    }

    @Override
    public ProfileResponseDTO setTheme(Long id, User user) throws UnauthorizedException, ThemeNotFoundException {
        if (user == null) {
            throw new UnauthorizedException("You are not logged in");
        }
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!theme.isPublished() && !user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        Profile profile = user.getProfile();
        profile.setThemeUsed(theme);
        Profile savedProfile = profileRepository.save(profile);
        return ProfileMapper.profileToResponseDTO(savedProfile);
    }

    @Override
    public void unsetThemeAndUseDefaultTheme(User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("You are not logged in");
        }
        Profile profile = user.getProfile();
        profile.setThemeUsed(null);
        profileRepository.save(profile);
    }
}
