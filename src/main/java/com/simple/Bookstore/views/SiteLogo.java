package com.simple.Bookstore.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteLogo {
    public static List<Map<String, Object>> getLogoLettersModelAttributes(String siteName) {
        char[] letters = siteName.toCharArray();
        List<Map<String, Object>> attributes = new ArrayList<>();
        for (int i = 0; i < letters.length; i++) {
            Map<String, Object> attribute = new HashMap<>();
            attribute.put("char", String.valueOf(letters[i]));
            attribute.put("id", "logo-letter-" + i);
            int rotate = switch (i) {
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
            attribute.put("rotate", rotate);
            int yOffset = switch (i) {
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
            attribute.put("y", yOffset);
            attribute.put("x", i * 38);
            attributes.add(attribute);
        }
        return attributes;
    }
}
