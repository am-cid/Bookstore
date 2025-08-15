package com.simple.Bookstore.Profile;

import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.ProfileDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    @Override
    public ProfileResponseDTO findByUsername(String username, User user) {
        if (user != null && user.getUsername().equals(username)) {
            return profileRepository
                    .findByUserUsername(username)
                    .map(ProfileDtoConverter::profileToResponseDTO)
                    .orElseThrow(() -> new UserNotFoundException(username));
        } else {
            return profileRepository
                    .findByUserUsernameAndIsPublicIsTrue(username)
                    .map(ProfileDtoConverter::profileToResponseDTO)
                    .orElseThrow(() -> new UserNotFoundException(username));
        }
    }
}
