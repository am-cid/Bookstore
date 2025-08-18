package com.simple.Bookstore.Theme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import com.simple.Bookstore.utils.ThemeMapper;
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
    private final ProfileRepository profileRepository;

    public ThemeServiceImpl(
            ThemeRepository themeRepository,
            UserRepository userRepository,
            ProfileRepository profileRepository
    ) {
        this.themeRepository = themeRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    @Override
    public Page<ThemeResponseDTO> getPublishedOrOwnedUnpublishedThemes(User user, Pageable pageable) {
        if (user == null) {
            return themeRepository
                    .findByPublishedIsTrue(pageable)
                    .map(ThemeMapper::themeToResponseDTO);
        } else {
            return themeRepository
                    .findByPublishedOrOwnedUnpublishedThemes(user, pageable)
                    .map(ThemeMapper::themeToResponseDTO);
        }
    }

    @Override
    public List<ThemeResponseDTO> getThemesByUser(User user) {
        return themeRepository
                .findByProfileUser(user)
                .stream()
                .map(ThemeMapper::themeToResponseDTO)
                .toList();
    }

    @Override
    public ThemeResponseDTO getPublishedThemeById(Long id) throws ThemeNotFoundException {
        return themeRepository
                .findByIdAndPublishedIsTrue(id)
                .map(ThemeMapper::themeToResponseDTO)
                .orElseThrow(() -> new ThemeNotFoundException(id));
    }

    @Override
    public ThemeResponseDTO createTheme(User user, ThemeRequestDTO request) {
        Profile profile = user.getProfile();
        Theme theme = ThemeMapper.requestDtoToTheme(profile, request);
        profileRepository.save(profile);
        Theme savedTheme = themeRepository.save(theme);
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    public ThemeResponseDTO updateTheme(Long id, ThemeRequestDTO request, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
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
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    public void deleteTheme(Long id, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new IllegalStateException("You are not user of theme " + theme.getName());
        }
        theme.getProfilesUsing().forEach(profile -> profile.getSavedThemes().remove(theme));
        themeRepository.delete(theme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO publishTheme(Long id, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new IllegalStateException("You are not user of theme " + theme.getName());
        }
        theme.setPublished(true);
        Theme savedTheme = themeRepository.save(theme);
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO makeThemePrivate(Long id, User user) {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new IllegalStateException("You are not user of theme " + theme.getName());
        }
        theme.setPublished(false);
        theme.getProfilesUsing().forEach(userUsing -> userUsing.getSavedThemes().remove(theme));
        theme.getProfilesUsing().clear();
        Theme savedTheme = themeRepository.save(theme);
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO saveThemeForUser(Long id, User user) {
        Theme theme = themeRepository
                .findByIdAndPublishedIsTrue(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        managedUser.getProfile().getSavedThemes().add(theme);
        theme.getProfilesUsing().add(user.getProfile());
        userRepository.save(managedUser);
        Theme savedTheme = themeRepository.save(theme);
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public void deleteThemeFromSavedThemes(Long id, User user) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        managedUser.getProfile().getSavedThemes().remove(theme);
        theme.getProfilesUsing().remove(user.getProfile());
        userRepository.save(managedUser);
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
                .map(ThemeMapper::themeToResponseDTO)
                .orElseThrow(() -> new ThemeNotFoundException(id));
    }

    @Override
    public Page<ThemeResponseDTO> searchThemes(String query, Long userId, Pageable pageable) {
        return themeRepository
                .searchThemes(query, userId, pageable)
                .map(ThemeMapper::projectionToResponseDTO);
    }

    @Override
    public String getThemeAsCss(Long id, User user, int steps) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!theme.isPublished() && !user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }

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
    public ThemeResponseDTO updateCssTheme(Long id, User user) throws IOException, ThemeNotFoundException {
        String cssContent = getThemeAsCss(id, user, 25);
        String filename = "variables.css";
        Path path = Paths.get("src/main/resources/static/css/" + filename);
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, cssContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return themeRepository.findById(id)
                .map(ThemeMapper::themeToResponseDTO)
                .orElseThrow(() -> new ThemeNotFoundException(id));

    }

    // HELPERS

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
