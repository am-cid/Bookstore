package com.simple.Bookstore.Theme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper yamlMapper;

    public ThemeServiceImpl(
            ThemeRepository themeRepository,
            UserRepository userRepository
    ) {
        this.themeRepository = themeRepository;
        this.userRepository = userRepository;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    @Override
    public Page<ThemeResponseDTO> getPublishedOrOwnedUnpublishedThemes(User user, Pageable pageable) {
        if (user == null) {
            return themeRepository
                    .findByPublishedIsTrue(pageable)
                    .map(this::themeToResponseDTO);
        } else {
            return themeRepository
                    .findByPublishedOrOwnedUnpublishedThemes(user, pageable)
                    .map(this::themeToResponseDTO);
        }
    }

    @Override
    public List<ThemeResponseDTO> getThemesByUser(User user) {
        return themeRepository
                .findByUser(user)
                .stream()
                .map(this::themeToResponseDTO)
                .toList();
    }

    @Override
    public ThemeResponseDTO getPublishedThemeById(Long id) throws ThemeNotFoundException {
        return themeRepository
                .findByIdAndPublishedIsTrue(id)
                .map(this::themeToResponseDTO)
                .orElseThrow(() -> new ThemeNotFoundException(id));
    }

    @Override
    public ThemeResponseDTO createTheme(User user, ThemeRequestDTO request) {
        Theme theme = requestDtoToTheme(user, request);
        Theme savedTheme = themeRepository.save(theme);
        return themeToResponseDTO(savedTheme);
    }

    @Override
    public ThemeResponseDTO updateTheme(Long id, ThemeRequestDTO request, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!theme.getUser().equals(user)) {
            throw new UnauthorizedException("You are not authorized to edit this theme");
        }
        theme.setName(request.name());
        theme.setBase00(request.base00());
        theme.setBase01(request.base01());
        theme.setBase02(request.base02());
        theme.setBase03(request.base03());
        theme.setBase04(request.base04());
        theme.setBase05(request.base05());
        theme.setBase06(request.base06());
        theme.setBase07(request.base07());
        Theme savedTheme = themeRepository.save(theme);
        return themeToResponseDTO(savedTheme);
    }

    @Override
    public void removeTheme(Long id, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getUser().getId())) {
            throw new IllegalStateException("You are not user of theme " + theme.getName());
        }
        themeRepository.delete(theme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO publishTheme(Long id, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getUser().getId())) {
            throw new IllegalStateException("You are not user of theme " + theme.getName());
        }
        theme.setPublished(true);
        Theme savedTheme = themeRepository.save(theme);
        return themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO makeThemePrivate(Long id, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getUser().getId())) {
            throw new IllegalStateException("You are not user of theme " + theme.getName());
        }
        theme.setPublished(false);
        theme.getUsersUsing().forEach(userUsing -> userUsing.getThemesInUse().remove(theme));
        theme.getUsersUsing().clear();
        Theme savedTheme = themeRepository.save(theme);
        return themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO saveThemeForUser(Long id, User user) {
        Theme theme = themeRepository
                .findByIdAndPublishedIsTrue(id)
                .orElseThrow(ThemeNotFoundException::new);
        user.getThemesInUse().add(theme);
        theme.getUsersUsing().add(user);
        userRepository.save(user);
        Theme savedTheme = themeRepository.save(theme);
        return themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public void removeThemeForUser(Long id, User user) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        user.getThemesInUse().remove(theme);
        theme.getUsersUsing().remove(user);
        userRepository.save(user);
        themeRepository.save(theme);
    }

    @Override
    public Theme loadThemeFromYaml(File yamlFile) throws IOException {
        return yamlMapper.readValue(yamlFile, Theme.class);
    }

    @Override
    public ThemeResponseDTO findThemeById(Long id) {
        return themeRepository
                .findById(id)
                .map(this::themeToResponseDTO)
                .orElseThrow(() -> new ThemeNotFoundException(id));
    }

    @Override
    public Page<ThemeResponseDTO> searchThemes(String query, Long userId, Pageable pageable) {
        return themeRepository
                .searchThemes(query, userId, pageable)
                .map(this::projectionToResponseDTO);
    }

    @Override
    public String getThemeAsCss(Long id, int steps) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        StringBuilder css = new StringBuilder(":root {\n");
        // base colors from base08 config
        for (int i = 0; i <= 7; i++) {
            String hex = theme.getColorByIndex(i);
            css.append(String.format("  --color-%02d: #%s;%n", i, hex));
        }

        // interpolated colors for circles after heading
        Color start = Color.decode("#" + theme.getBase03());
        Color end = Color.decode("#" + theme.getBase04());
        List<String> interpolated = getInterpolatedColors(start, end, 27)
                .subList(1, 26); // exclude original endpoints
        IntStream.range(0, interpolated.size())
                .forEach(i -> css.append(String.format("  --interpolated-color-%02d: %s;%n", i, interpolated.get(i))));

        // genre text color (must be darker than base05 which is genre bg)
        List<String> genreTextColors = getGenreTextColors(
                theme.getBase03(),
                theme.getBase04(),
                theme.getBase05()
        );
        IntStream.range(0, genreTextColors.size())
                .forEach(i -> css.append(
                        String.format("  --genre-text-color-%02d: %s;%n", i, genreTextColors.get(i))
                ));

        css.append("}");
        return css.toString();
    }

    @Override
    public void updateCssTheme(Long id) throws IOException {
        String cssContent = getThemeAsCss(id, 25);
        String filename = "generated-variables.css";
        Path path = Paths.get("src/main/resources/static/css/" + filename);
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, cssContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // HELPERS
    private ThemeResponseDTO themeToResponseDTO(Theme theme) {
        return new ThemeResponseDTO(
                theme.getId(),
                theme.getName(),
                theme.getUser().getId(),
                theme.getUser().getUsername(),
                theme.getUser().getDisplayName(),
                theme.getBase00(),
                theme.getBase01(),
                theme.getBase02(),
                theme.getBase03(),
                theme.getBase04(),
                theme.getBase05(),
                theme.getBase06(),
                theme.getBase07()
        );
    }

    private Theme requestDtoToTheme(User user, ThemeRequestDTO request) {
        Theme theme = new Theme();
        theme.setUser(user);
        theme.setName(request.name());
        theme.setPublished(request.published());
        theme.setBase00(request.base00());
        theme.setBase01(request.base01());
        theme.setBase02(request.base02());
        theme.setBase03(request.base03());
        theme.setBase04(request.base04());
        theme.setBase05(request.base05());
        theme.setBase06(request.base06());
        theme.setBase07(request.base07());
        return theme;
    }

    private ThemeResponseDTO projectionToResponseDTO(ThemeProjection projection) {
        return new ThemeResponseDTO(
                projection.getId(),
                projection.getName(),
                projection.getUserId(),
                projection.getUsername(),
                projection.getUserDisplayName(),
                projection.getBase00(),
                projection.getBase01(),
                projection.getBase02(),
                projection.getBase03(),
                projection.getBase04(),
                projection.getBase05(),
                projection.getBase06(),
                projection.getBase07()
        );
    }

    /**
     * interpolates between two colors (inclusive)
     *
     * @param start inclusive starting color for the interpolation
     * @param end   inclusive ending color for the interpolation
     * @param steps amount of colors generated
     * @return list of interpolated colors
     */
    private List<String> getInterpolatedColors(Color start, Color end, int steps) {
        List<String> colors = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            float ratio = i / (float) (steps - 1);
            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
            colors.add(String.format("#%02X%02X%02X", r, g, b));
        }
        return colors;
    }

    /**
     * Darkens passed in colors using custom ratio for use as text color in
     * listing genres.
     *
     * @param genreBg list of background colors the genre ovals will use
     * @return list of darkened colors that the genre ovals' text will use
     */
    private List<String> getGenreTextColors(String... genreBg) {
        List<Color> genreBgDarkenedStrings = Arrays
                .stream(genreBg)
                .map(c -> Color.decode("#" + c).darker())
                .toList();
        List<String> result = new ArrayList<>();
        for (Color c : genreBgDarkenedStrings) {
            result.add(String.format(
                    "#%02X%02X%02X",
                    (int) (c.getRed() * 0.3),
                    (int) (c.getGreen() * 0.7),
                    (int) (c.getBlue() * 0.3)
            ));
        }
        return result;
    }
}
