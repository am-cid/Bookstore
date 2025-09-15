package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;

import java.time.LocalDateTime;
import java.util.Set;

public record BookSearchResultDTO(
        Long id,
        String title,
        String author,
        String description,
        LocalDateTime date,
        Double averageRating,
        String frontImage,
        String backImage,
        String spineImage,
        Set<String> contentImages,
        Set<Genre> genres
) {
}
