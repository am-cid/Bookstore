package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;

import java.util.Set;

public record BookSearchResultDTO(
        Long id,
        String title,
        String author,
        String description,
        Set<Genre> genres,
        Double averageRating,
        String frontImage,
        String backImage,
        String spineImage,
        Set<String> contentImages
) {
}
