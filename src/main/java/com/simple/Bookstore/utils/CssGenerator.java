package com.simple.Bookstore.utils;

import java.awt.*;
import java.util.List;

public class CssGenerator {
    /**
     * Returns the inline &lt;style&gt;...&lt;/style&gt; of the given base08 theme.
     *
     * @param colors String / null: when not overriding default theme, this is null.
     * @return String / null: either the inline css as string to be used by Thymeleaf
     * in th:utext, or null, indicating there was an error parsing the hex code of
     * the colors given
     */
    public static String toInlineCss(List<String> colors) {
        if (colors == null || colors.isEmpty())
            return null;

        List<String> interpolatedColors = ColorUtils
                .getInterpolatedColors(
                        Color.decode("#" + colors.get(3)),
                        Color.decode("#" + colors.get(4)),
                        27
                )
                .subList(1, 26);
        List<String> genreBgColors = ColorUtils
                .adjustGenreBgColors(
                        Color.decode("#" + colors.get(3)),
                        Color.decode("#" + colors.get(4)),
                        Color.decode("#" + colors.get(5))
                );
        List<String> genreTextColors = ColorUtils
                .getGenreTextColors(
                        Color.decode("#" + colors.get(3)),
                        Color.decode("#" + colors.get(4)),
                        Color.decode("#" + colors.get(5))
                );

        StringBuilder css = new StringBuilder()
                .append("<!-- START GENERATED CSS -->")
                .append("<style type=\"text/css\">\n")
                .append(":root {\n");

        if (!appendColors(css, "color", colors))
            return null;
        if (!appendColors(css, "interpolated-color", interpolatedColors))
            return null;
        if (!appendColors(css, "genre-bg-color", genreBgColors))
            return null;
        if (!appendColors(css, "genre-text-color", genreTextColors))
            return null;

        boolean isDarkTheme = ColorUtils.isDarkTheme(
                Color.decode("#" + colors.getFirst()),
                Color.decode("#" + colors.getLast())
        );
        // genreTextColors outline color
        for (int i = 0; i < genreBgColors.size(); i++) {
            Color c = Color.decode("#" + genreTextColors.get(i));
            css.append("    --genre-outline-color-")
                    .append(String.format("%02d", i))
                    .append(": #")
                    .append(ColorUtils.isBright(c)
                            ? (isDarkTheme ? colors.get(7) : colors.get(0))
                            : (isDarkTheme ? colors.get(0) : colors.get(7))
                    )
                    .append(";\n");
        }

        // font colors
        css.append("    --font-color: ")
                .append(isDarkTheme ? "white" : "black")
                .append(";\n");
        css.append("    --link-color: ")
                .append(isDarkTheme ? "lightblue" : "blue")
                .append(";\n");

        return css.append("}\n")
                .append("</style>\n")
                .append("<!-- END GENERATED CSS -->\n")
                .toString();
    }

    private static boolean appendColors(
            StringBuilder sb,
            String basename,
            List<String> colors
    ) {
        for (int i = 0; i < colors.size(); i++) {
            String color = colors.get(i);
            if (ColorUtils.isValidHexColor(color)) {
                sb.append("    --").append(basename).append("-")
                        .append(String.format("%02d", i))
                        .append(": #")
                        .append(colors.get(i))
                        .append(";\n");
            } else {
                return false;
            }
        }
        return true;
    }

}
