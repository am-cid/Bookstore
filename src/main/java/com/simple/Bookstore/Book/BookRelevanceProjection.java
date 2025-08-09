package com.simple.Bookstore.Book;

public interface BookRelevanceProjection {
    Long getId();

    String getTitle();

    String getAuthor();

    String getDescription();

    String getFrontImage();

    String getBackImage();

    String getSpineImage();

    Double getAverageRating();

    Long getReviewCount();
}
