package com.simple.Bookstore.Book;

import java.time.LocalDateTime;

public interface BookSearchResultProjection {
    Long getId();

    String getTitle();

    String getAuthor();

    String getDescription();

    LocalDateTime getDate();

    String getFrontImage();

    String getBackImage();

    String getSpineImage();

    Double getAverageRating();

    String[] getGenres();

    String[] getContentImages();

}
