package com.simple.Bookstore.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorUtils {
    private static final double RED_DARKEN_RATIO = 0.30;
    private static final double GREEN_DARKEN_RATIO = 0.70;
    private static final double BLUE_DARKEN_RATIO = 0.30;


    /**
     * interpolates between two colors (inclusive)
     *
     * @param start inclusive starting color for the interpolation
     * @param end   inclusive ending color for the interpolation
     * @param steps amount of colors generated
     * @return list of interpolated colors. note that the colors are hex colors
     * without the pound (#) e.g "1A2B3C"
     */
    public static List<String> getInterpolatedColors(Color start, Color end, int steps) {
        List<String> colors = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            float ratio = i / (float) (steps - 1);
            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
            colors.add(String.format("%02X%02X%02X", r, g, b));
        }
        return colors;
    }

    /**
     * Darkens passed in colors using custom ratio for use as text color in
     * listing genres.
     *
     * @param genreBg list of background colors the genre ovals will use
     * @return list of darkened colors that the genre ovals' text will use.
     * note that the colors are hex colors without the pound (#) e.g "1A2B3C"
     */
    public static List<String> getGenreTextColors(Color... genreBg) {
        return Arrays
                .stream(genreBg)
                .map(c -> String.format(
                                "%02X%02X%02X",
                                (int) (c.getRed() * RED_DARKEN_RATIO),
                                (int) (c.getGreen() * GREEN_DARKEN_RATIO),
                                (int) (c.getBlue() * BLUE_DARKEN_RATIO)
                        )
                )
                .toList();
    }

    /**
     * @param hex color with or without preceding pound (#)
     * @return whether the given hex string, with or without preceding pound
     * (#), is a valid hex color
     */
    public static boolean isValidHexColor(String hex) {
        return hex != null && hex.matches("#?[0-9a-fA-F]{6}");
    }
}
