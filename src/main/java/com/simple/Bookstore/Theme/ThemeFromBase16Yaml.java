package com.simple.Bookstore.Theme;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ThemeFromBase16Yaml(
        String scheme,
        String author,
        String base00,
        String base01,
        String base02,
        String base03,
        String base04,
        String base05,
        String base06,
        String base07
) {
}
