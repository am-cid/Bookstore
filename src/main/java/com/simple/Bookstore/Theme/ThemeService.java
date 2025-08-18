package com.simple.Bookstore.Theme;

import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ThemeService {
    ThemeResponseDTO findDefaultTheme() throws IllegalStateException;

    Page<ThemeResponseDTO> findPublishedOrOwnedUnpublishedThemes(User user, Pageable pageable);

    List<ThemeResponseDTO> findThemesByUser(User user);

    ThemeResponseDTO findThemeById(Long id);

    ThemeResponseDTO findPublishedThemeById(Long id);

    ThemeResponseDTO createTheme(User user, ThemeRequestDTO request);

    ThemeResponseDTO updateTheme(Long id, ThemeRequestDTO request, User user);

    ThemeResponseDTO publishTheme(Long id, User user);

    ThemeResponseDTO makeThemePrivate(Long id, User user);

    ThemeResponseDTO saveThemeForUser(Long id, User user);

    ThemeResponseDTO setThemeForUser(Long id, User user);

    void deleteTheme(Long id, User user);

    void deleteThemeFromSavedThemes(Long id, User user);

    Theme loadThemeFromYaml(File yamlFile) throws IOException;

    Page<ThemeResponseDTO> searchThemes(String query, Long userId, Pageable pageable);

    String getThemeAsCss(Long id, User user, int steps);

    ThemeResponseDTO updateCssTheme(Long id, User user) throws IOException, ThemeNotFoundException;
}
