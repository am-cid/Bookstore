package com.simple.Bookstore.Book;

import java.time.LocalDateTime;

public interface BookRelevanceProjection {
    Long getId();

    String getTitle();

    String getAuthor();

    String getDescription();

    LocalDateTime getDate();

    String getFrontImage();

    String getBackImage();

    String getSpineImage();

    Double getAverageRating();

    Long getReviewCount();
}
