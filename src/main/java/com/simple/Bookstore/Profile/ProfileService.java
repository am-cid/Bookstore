package com.simple.Bookstore.Profile;

import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfileService {
    ProfileResponseDTO findOwn(User user) throws UnauthorizedException;

    ProfileResponseDTO findByUsername(String username, User user);

    ProfileResponseDTO setTheme(Long id, User user) throws UnauthorizedException, ThemeNotFoundException;

    void unsetThemeAndUseDefaultTheme(User user) throws UnauthorizedException;

    ProfileResponseDTO saveTheme(Long id, User user) throws UnauthorizedException, ThemeNotFoundException;

    void unsaveTheme(Long id, User user) throws UnauthorizedException, ThemeNotFoundException;

    ProfileResponseDTO saveBook(Long id, User user) throws UnauthorizedException, BookNotFoundException;

    void unsaveBook(Long id, User user) throws UnauthorizedException, BookNotFoundException;

    Page<ProfileResponseDTO> searchProfiles(String query, User user, Pageable pageable);

    Profile updateProfile(Profile profile, ProfileEditRequestDTO request);
}
