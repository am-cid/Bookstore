package com.simple.Bookstore.utils.presentation;

import java.util.List;
import java.util.stream.IntStream;

public class SiteLogoFactory {
    /**
     * Adds model attributes for logo letters based on the passed in site name string
     *
     * @param siteName name of website
     * @return list of site logo letters with model attributes
     */
    public static List<SiteLogoLetter> withModelAttributes(String siteName) {
        return IntStream
                .range(0, siteName.length())
                .mapToObj(i -> {
                    String letter = String.valueOf(siteName.charAt(i));
                    String htmlId = "logo-letter-%d".formatted(i);
                    Integer rotate = switch (i) {
                        case 1,
                             7 ->
                                -10;
                        case 2 ->
                                10;
                        case 4 ->
                                5;
                        case 5 ->
                                -5;
                        default ->
                                0;
                    };
                    Integer yOffset = switch (i) {
                        case 1,
                             6 ->
                                2;
                        case 2,
                             7 ->
                                -2;
                        case 3 ->
                                1;
                        case 5 ->
                                -3;
                        default ->
                                0;
                    };
                    Integer xOffset = i * 38;
                    return new SiteLogoLetter(letter, htmlId, rotate, xOffset, yOffset);
                })
                .toList();
    }
}
