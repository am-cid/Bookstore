package com.simple.Bookstore.utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.pow;

public class ColorUtils {
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
     * Darkens/lightens passed in bg colors to lower contrast e.g. if the color
     * is bright, it will be brightened, and vice versa.
     *
     * @param genreBg list of background colors the genre ovals will use
     * @return list of darkened colors that the genre ovals' text will use.
     * note that the colors are hex colors without the pound (#) e.g "1A2B3C"
     */
    public static List<String> adjustGenreBgColors(Color... genreBg) {
        return Arrays
                .stream(genreBg)
                .map(c -> isBright(c)
                                ? String.format(
                                "%02X%02X%02X",
                                c.brighter().getRed(),
                                c.brighter().getGreen(),
                                c.brighter().getBlue()
                        )
                                : String.format(
                                "%02X%02X%02X",
                                c.darker().getRed(),
                                c.darker().getGreen(),
                                c.darker().getBlue()
                        )
                )
                .toList();
    }

    /**
     * Darkens/lightens passed in colors to heighten contrast the given colors e.g. if
     * the color is bright, it will be darkened, and vice versa.
     *
     * @param genreBg list of background colors the genre ovals will use
     * @return list of darkened colors that the genre ovals' text will use.
     * note that the colors are hex colors without the pound (#) e.g "1A2B3C"
     */
    public static List<String> getGenreTextColors(Color... genreBg) {
        return Arrays
                .stream(genreBg)
                .map(c -> isBright(c)
                                ? String.format(
                                "%02X%02X%02X",
                                c.darker().darker().getRed(),
                                c.darker().darker().getGreen(),
                                c.darker().darker().getBlue()
                        )
                                : String.format(
                                "%02X%02X%02X",
                                c.brighter().brighter().getRed(),
                                c.brighter().brighter().getGreen(),
                                c.brighter().brighter().getBlue()
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

    /**
     * checks if a base8 theme is dark theme by comparing perceived brightness
     * of the first and last color of a theme.
     *
     * @param base00 first color of a theme
     * @param base07 first color of a theme
     * @return whether a dark theme or not
     */
    public static boolean isDarkTheme(Color base00, Color base07) {
        return perceivedBrightness(base00) > perceivedBrightness(base07);
    }

    /**
     * check if color is bright or not according to perceived brightness.
     * not to be confused with luminance.
     * <p>
     * Has a value between 0 and 100 where 50 is the middle grey.
     * <p>
     * <a href="https://stackoverflow.com/a/56678483">source</a>
     *
     * @param color color to check
     * @return true if perceived brightness is over 50, else false
     */
    public static boolean isBright(Color color) {
        return perceivedBrightness(color) > 50;
    }

    /**
     * Gets the percieved brightness of a color. not to be confused with
     * luminance.
     * <p>
     * Has a value between 0 and 100 where 50 is the middle grey.
     * <p>
     * <a href="https://stackoverflow.com/a/56678483">source</a>
     *
     * @param color color to check
     * @return perceived brightness value between 0 and 100
     */
    public static double perceivedBrightness(Color color) {
        double y = luminance(color);
        if (y <= (double) 216 / 24389) { // The CIE standard states 0.008856 but 216/24389 is the intent for 0.008856451679036
            return y * ((double) 24389 / 27); // The CIE standard states 903.3, but 24389/27 is the intent, making 903.296296296296296
        } else {
            return Math.pow(y, ((double) 1 / 3)) * 116 - 16;
        }
    }

    /**
     * Get perceived brightness of color (luminance).
     * <p>
     * <a href="https://stackoverflow.com/a/56678483">source</a>
     *
     * @param color color to get the luminance of
     * @return luminance value between 0.0 and 1.0
     */
    public static double luminance(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;
        return gammaEncodedToLinear(r) * 0.2126 +
                gammaEncodedToLinear(g) * 0.7152 +
                gammaEncodedToLinear(b) * 0.0722;
    }

    /**
     * gets the linear value of a gamma encoded rgb channel
     * <p>
     * <a href="https://stackoverflow.com/a/56678483">source</a>
     *
     * @param colorChannel gamma encoded color channel to convert
     * @return linear value of gamma encoded color channel
     */
    public static double gammaEncodedToLinear(double colorChannel) {
        if (colorChannel <= 0.04045) {
            return colorChannel / 12.92;
        } else {
            return pow(((colorChannel + 0.055) / 1.055), 2.4);
        }
    }
}
