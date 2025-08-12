package com.simple.Bookstore.Theme;

import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ThemeService {
    Page<ThemeResponseDTO> getPublishedOrOwnedUnpublishedThemes(User user, Pageable pageable);

    List<ThemeResponseDTO> getThemesByUser(User user);

    ThemeResponseDTO getPublishedThemeById(Long id);

    ThemeResponseDTO createTheme(User user, ThemeRequestDTO request);

    ThemeResponseDTO updateTheme(Long id, ThemeRequestDTO request, User user);

    ThemeResponseDTO publishTheme(Long id, User user);

    ThemeResponseDTO makeThemePrivate(Long id, User user);

    ThemeResponseDTO saveThemeForUser(Long id, User user);

    void removeTheme(Long id, User user);

    void removeThemeForUser(Long id, User user);

    Theme loadThemeFromYaml(File yamlFile) throws IOException;

    ThemeResponseDTO findThemeById(Long id);

    Page<ThemeResponseDTO> searchThemes(String query, Long userId, Pageable pageable);

    String getThemeAsCss(Long id, int steps);

    void updateCssTheme(Long id) throws IOException;
}
