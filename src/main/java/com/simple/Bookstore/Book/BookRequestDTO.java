package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BookRequestDTO(
        @Size(max = 255, message = "Title must not exceed 255 characters.")
        String title,
        @Size(max = 255, message = "Author must not exceed 255 characters.")
        String author,
        @Size(max = 2000, message = "Stop yapping! 2000 chars only!")
        String description,
        Set<Genre> genres,
        String frontImage,
        String backImage,
        String spineImage,
        Set<String> contentImages
) {
}
