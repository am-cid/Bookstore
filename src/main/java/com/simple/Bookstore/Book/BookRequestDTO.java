package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;

import java.util.Set;

public record BookRequestDTO(
        String title,
        String author,
        String description,
        Set<Genre> genres,
        String frontImage,
        String backImage,
        String spineImage,
        Set<String> contentImages
) {
}
