package com.simple.Bookstore.Profile;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Exceptions.UserNotFoundException;
import com.simple.Bookstore.Theme.Theme;
import com.simple.Bookstore.Theme.ThemeRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import com.simple.Bookstore.utils.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final BookRepository bookRepository;

    @Override
    public ProfileResponseDTO findOwn(User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException();
        }
        return ProfileMapper.profileToResponseDTO(user.getProfile());
    }

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
    @Transactional
    public ProfileResponseDTO setTheme(Long id, User user) throws UnauthorizedException, ThemeNotFoundException {
        if (user == null) {
            throw new UnauthorizedException();
        }
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!theme.isPublished() && !user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        Profile profile = user.getProfile();
        profile.setUsedTheme(theme);
        Profile savedProfile = profileRepository.save(profile);
        theme.getUsedByProfiles().add(profile);
        themeRepository.save(theme);
        return ProfileMapper.profileToResponseDTO(savedProfile);
    }

    @Override
    public void unsetThemeAndUseDefaultTheme(User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException();
        }
        Profile profile = user.getProfile();
        Theme usedTheme = profile.getUsedTheme();
        profile.setUsedTheme(null);
        Profile savedProfile = profileRepository.save(profile);
        usedTheme.getUsedByProfiles().remove(savedProfile);
    }

    @Override
    @Transactional
    public ProfileResponseDTO saveTheme(Long id, User user) throws UnauthorizedException, ThemeNotFoundException {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!theme.isPublished() && !user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        Profile profile = managedUser.getProfile();
        profile.getSavedThemes().add(theme);
        Profile savedProfile = profileRepository.save(profile);
        theme.getSavedByProfiles().add(savedProfile);
        themeRepository.save(theme);
        return ProfileMapper.profileToResponseDTO(savedProfile);
    }

    @Override
    @Transactional
    public void unsaveTheme(Long id, User user) throws UnauthorizedException {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!theme.isPublished() && !user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        Profile profile = managedUser.getProfile();
        profile.getSavedThemes().remove(theme);
        Profile savedProfile = profileRepository.save(profile);
        theme.getSavedByProfiles().remove(savedProfile);
        themeRepository.save(theme);
    }

    @Override
    @Transactional
    public ProfileResponseDTO saveBook(Long id, User user) throws UnauthorizedException, BookNotFoundException {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        Profile profile = managedUser.getProfile();
        profile.getSavedBooks().add(book);
        Profile savedProfile = profileRepository.save(profile);
        book.getSavedByProfiles().add(savedProfile);
        bookRepository.save(book);
        return ProfileMapper.profileToResponseDTO(savedProfile);
    }

    @Override
    @Transactional
    public void unsaveBook(Long id, User user) throws UnauthorizedException, BookNotFoundException {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        Profile profile = managedUser.getProfile();
        profile.getSavedBooks().remove(book);
        Profile savedProfile = profileRepository.save(profile);
        book.getSavedByProfiles().remove(savedProfile);
        bookRepository.save(book);
    }

    @Override
    public Page<ProfileResponseDTO> searchProfiles(Optional<String> query, User user, Pageable pageable) {
        return profileRepository
                .searchProfiles(query.orElse(null), user == null ? null : user.getId(), pageable)
                .map(ProfileMapper::projectionToResponseDTO);
    }

    @Override
    public Profile updateProfile(Profile profile, ProfileEditRequestDTO request) {
        profile.setDisplayName(request.displayName());
        profile.setPublic(request.isPublic());
        return profileRepository.save(profile);
    }
}
