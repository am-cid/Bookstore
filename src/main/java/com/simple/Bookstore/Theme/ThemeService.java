package com.simple.Bookstore.Theme;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.User.User;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ThemeService {
    Page<ThemeResponseDTO> findPublishedOrOwnedUnpublishedThemes(User user, Pageable pageable);

    List<ThemeResponseDTO> findThemesByUser(User user);

    Page<ThemeResponseDTO> findThemesByUser(User user, Pageable pageable);

    ThemeResponseDTO findThemeById(Long id);

    ThemeResponseDTO findPublishedThemeById(Long id);

    ThemeResponseDTO createTheme(User user, ThemeRequestDTO request);

    ThemeResponseDTO updateTheme(Long id, ThemeRequestDTO request, User user) throws ThemeNotFoundException;

    ThemeResponseDTO publishTheme(Long id, User user) throws ThemeNotFoundException;

    ThemeResponseDTO makeThemePrivate(Long id, User user) throws ThemeNotFoundException;

    ThemeResponseDTO saveThemeForUser(Long id, User user) throws ThemeNotFoundException;

    void deleteTheme(Long id, User user) throws ThemeNotFoundException;

    void deleteThemeFromSavedThemes(Long id, User user) throws ThemeNotFoundException;

    /**
     * Use to create a request to create a theme from a yaml scheme.
     *
     * @param yamlFile
     * @param published
     * @param customName        String/null - leave empty to use yamlScheme.scheme as the theme name
     * @param customDescription String/null - leave empty to use yamlScheme.author as the theme description
     * @return
     * @throws IOException
     */
    ThemeRequestDTO loadThemeFromYaml(
            File yamlFile,
            boolean published,
            @Nullable String customName,
            @Nullable String customDescription
    ) throws IOException;

    Page<ThemeResponseDTO> searchThemes(String query, Long userId, Pageable pageable) throws ThemeNotFoundException;

    String getThemeAsCss(Long id, User user, int steps) throws ThemeNotFoundException;

    /**
     * @param user User / null: when anonymous, this is null
     * @return ThemeResponseDTO / null: Returns the currently used theme by the
     * authenticated user. If the user is anonymous or is an authenticated user
     * but did not override the default theme, this will return null, which
     * indicates that there is no override for the default theme.
     * @throws IllegalStateException when the database cannot find the default
     *                               theme, which should be there as the first
     *                               entry
     */
    ThemeResponseDTO findUsedTheme(User user);

    List<ThemeResponseDTO> findSavedThemes(User user);

    List<Long> findSavedThemeIds(User user);

    Page<ThemeResponseDTO> findSavedThemes(User user, Pageable pageable);
}
