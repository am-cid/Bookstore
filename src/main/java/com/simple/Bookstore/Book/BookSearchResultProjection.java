package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;

import java.time.LocalDateTime;
import java.util.Set;

public interface BookSearchResultProjection {
    String getTitle();

    String getAuthor();

    String getDescription();

    LocalDateTime getDate();

    String getFrontImage();

    String getBackImage();

    String getSpineImage();

    Double getAverageRating();

    Long getId();

    Set<Genre> getGenres();

    Set<String> getContentImages();

}
