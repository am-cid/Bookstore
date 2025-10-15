package com.simple.Bookstore.Theme;

import com.simple.Bookstore.utils.ColorUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ThemeRequestDTO(
        @NotBlank(message = "Theme must have a name")
        @Size(min = 3, max = 20, message = "Theme name must be between 3 and 100 characters")
        String name,

        @Size(max = 2000, message = "Stop yapping! 2000 chars only!")
        String description,

        Boolean published,

        @NotBlank(message = "Theme must have color 0")
        String base00,

        @NotBlank(message = "Theme must have color 1")
        String base01,

        @NotBlank(message = "Theme must have color 2")
        String base02,

        @NotBlank(message = "Theme must have color 3")
        String base03,

        @NotBlank(message = "Theme must have color 4")
        String base04,

        @NotBlank(message = "Theme must have color 5")
        String base05,

        @NotBlank(message = "Theme must have color 6")
        String base06,

        @NotBlank(message = "Theme must have color 7")
        String base07
) {
    public static ThemeRequestDTO empty() {
        return new ThemeRequestDTO(
                null, null, false,
                null, null, null, null, null, null, null, null
        );
    }

    public static ThemeRequestDTO defaultTheme() {
        return new ThemeRequestDTO(
                null,
                null,
                true,
                "A40E60",
                "FF0000",
                "F61590",
                "F88B9E",
                "FFD376",
                "AAFFB1",
                "FFE2E7",
                "FFF2D3"
        );
    }

    /**
     * Hex colors from the form has <code>#</code> prepended. Internal theme
     * representation only has the 6 character hex. This will create a
     * <p>
     * It will not touch colors that are already clean.
     *
     * @return ThemeRequestDTO with clean colors.
     */
    public ThemeRequestDTO withCleanHexColors() {
        return new ThemeRequestDTO(
                name,
                description,
                published,
                ColorUtils.cleanHexColor(base00),
                ColorUtils.cleanHexColor(base01),
                ColorUtils.cleanHexColor(base02),
                ColorUtils.cleanHexColor(base03),
                ColorUtils.cleanHexColor(base04),
                ColorUtils.cleanHexColor(base05),
                ColorUtils.cleanHexColor(base06),
                ColorUtils.cleanHexColor(base07)
        );
    }

    public boolean isEmpty() {
        return name == null
                && description == null
                && base00 == null
                && base01 == null
                && base02 == null
                && base03 == null
                && base04 == null
                && base05 == null
                && base06 == null
                && base07 == null;
    }
}
