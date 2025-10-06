package com.simple.Bookstore.Theme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.simple.Bookstore.Exceptions.ThemeNotFoundException;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import com.simple.Bookstore.utils.ColorUtils;
import com.simple.Bookstore.utils.ThemeMapper;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    public String getThemeAsInlineCss(Long id, User user, int steps) throws ThemeNotFoundException {
        Theme theme = themeRepository
                .findById(id)
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
        List<String> interpolated = ColorUtils.getInterpolatedColors(start, end, 27)
                .subList(1, 26); // exclude original endpoints
        IntStream.range(0, interpolated.size())
                .forEach(i -> css.append(String.format("  --interpolated-color-%02d: %s;%n", i, interpolated.get(i))));

        // genre text color (must be darker than base05 which is genre bg)
        List<String> genreTextColors = ColorUtils.getGenreTextColors(
                Color.decode("#" + theme.getBase03()),
                Color.decode("#" + theme.getBase04()),
                Color.decode("#" + theme.getBase05())
        );
        IntStream.range(0, genreTextColors.size())
                .forEach(i -> css.append(
                        String.format("  --genre-text-color-%02d: %s;%n", i, genreTextColors.get(i))
                ));

        // font colors
        boolean isDarkTheme = ColorUtils.isDarkTheme(
                Color.decode("#" + theme.getBase00()),
                Color.decode("#" + theme.getBase07())
        );
        css.append(String.format("  --font-color: %s;%n", isDarkTheme ? "white" : "black"));
        css.append(String.format("  --link-color: %s;%n", isDarkTheme ? "lightblue" : "blue"));

        css.append("}");
        return css.toString();
    }

    @Override
    public Optional<ThemeResponseDTO> findPublishedOrOwnedThemeById(User user, Long themeId) {
        Long profileId = user != null ? user.getProfile().getId() : null;
        return themeRepository
                .findPublishedOrOwnedThemeById(themeId, profileId);
    }

    @Override
    public ThemeResponseDTO findUsedTheme(User user) {
        if (user == null || user.getProfile().getUsedTheme() == null)
            return null;

        return ThemeMapper.themeToResponseDTO(
                user.getProfile().getUsedTheme()
        );
    }

    @Override
    public ThemeRequestDTO loadThemeFromYaml(
            File yamlFile,
            boolean published,
            @Nullable String customName,
            @Nullable String customDescription
    ) throws IOException {
        ThemeFromBase16Yaml yamlScheme = yamlMapper.readValue(yamlFile, ThemeFromBase16Yaml.class);
        return ThemeMapper.base16YamlToRequestDTO(yamlScheme, published, customName, customDescription);
    }

    @Override
    public ThemeResponseDTO findPublishedThemeById(Long id) throws ThemeNotFoundException {
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
    public ThemeResponseDTO updateTheme(Long id, ThemeRequestDTO request, User user)
            throws ThemeNotFoundException {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        theme.setName(request.name());
        theme.setDescription(request.description());
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
    @Transactional
    public ThemeResponseDTO publishTheme(Long id, User user)
            throws ThemeNotFoundException {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        theme.setPublished(true);
        Theme savedTheme = themeRepository.save(theme);
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO makeThemePrivate(Long id, User user) throws ThemeNotFoundException {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        theme.setPublished(false);
        theme.getSavedByProfiles().forEach(profile -> profile.getSavedThemes().remove(theme));
        theme.getSavedByProfiles().clear();
        theme.getUsedByProfiles().forEach(profile -> profile.setUsedTheme(null));
        theme.getUsedByProfiles().clear();
        Theme savedTheme = themeRepository.save(theme);
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    @Transactional
    public ThemeResponseDTO saveThemeForUser(Long id, User user) throws ThemeNotFoundException {
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
        Theme savedTheme = themeRepository.save(theme);
        return ThemeMapper.themeToResponseDTO(savedTheme);
    }

    @Override
    public void deleteTheme(Long id, User user)
            throws ThemeNotFoundException {
        Theme theme = themeRepository
                .findById(id)
                .orElseThrow(() -> new ThemeNotFoundException(id));
        if (!user.getId().equals(theme.getProfile().getUser().getId())) {
            throw new ThemeNotFoundException(id);
        }
        theme.getSavedByProfiles().forEach(profile -> profile.getSavedThemes().remove(theme));
        theme.getUsedByProfiles().forEach(profile -> profile.setUsedTheme(null));
        themeRepository.delete(theme);
    }

    @Override
    @Transactional
    public void deleteThemeFromSavedThemes(Long id, User user) throws ThemeNotFoundException {
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
    public List<ThemeResponseDTO> findLatestNPublishedOrOwnedThemes(User user, int n) {
        return themeRepository
                .findLatestNPublishedOrOwnedThemes(user, n);
    }

    @Override
    public List<ThemeResponseDTO> findThemesByUser(User user) {
        return themeRepository
                .findByProfileUserOrderByName(user)
                .stream()
                .map(ThemeMapper::themeToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<ThemeResponseDTO> findSavedThemes(User user) {
        if (user == null)
            return List.of();
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        return managedUser
                .getProfile()
                .getSavedThemes()
                .stream()
                .map(ThemeMapper::themeToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<Long> findSavedThemeIds(User user) {
        if (user == null)
            return List.of();
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        return themeRepository
                .findProfileSavedThemeIds(managedUser.getProfile().getId());
    }

    @Override
    public Page<ThemeResponseDTO> findPublishedOrOwnedUnpublishedThemes(User user, Pageable pageable) {
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
    public Page<ThemeResponseDTO> findThemesByUser(@NonNull User profileUser, User visitor, Pageable pageable) {
        Long profileId = profileUser.getProfile().getId();
        Long visitorProfileId = visitor != null ? visitor.getProfile().getId() : null;
        return themeRepository
                .findPublishedOrOwnedThemeByProfileId(profileId, visitorProfileId, pageable);
    }

    @Override
    public Page<ThemeResponseDTO> searchThemes(
            Optional<String> query,
            User user,
            Pageable pageable
    ) throws ThemeNotFoundException {
        Long profileId = user != null ? user.getProfile().getId() : null;
        return themeRepository
                .searchThemes(query.orElse(null), profileId, pageable)
                .map(ThemeMapper::projectionToResponseDTO);
    }

    @Override
    @Transactional
    public Page<ThemeResponseDTO> findSavedThemes(User user, Pageable pageable) {
        if (user == null)
            return Page.empty(pageable);
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepository.findById(user.getId()).get();
        return themeRepository.findProfileSavedThemes(
                managedUser.getProfile().getId(),
                pageable
        );
    }
}
