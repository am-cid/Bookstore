package com.simple.Bookstore.Book;

import java.time.LocalDateTime;

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

    ///  Comma separated string
    String[] getGenres();

    ///  Comma separated string
    String[] getContentImages();

}
