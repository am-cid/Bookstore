package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;

import java.util.Set;

public record BookPreviewDTO(
        Long id,
        String title,
        String author,
        String frontImage,
        Double averageRating,
        Set<Genre> genres
) {
}
