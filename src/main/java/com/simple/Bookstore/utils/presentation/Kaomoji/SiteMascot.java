package com.simple.Bookstore.utils.presentation.Kaomoji;

public record SiteMascot(
        String part1,
        String part2,
        /// affects spacing between part1 and part2
        Integer widthX1,
        /// affects spacing between part1 and part2
        Integer widthX2,
        /// affects font size (in css, it's set to be 62)
        Integer height
) {
}
