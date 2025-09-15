package com.simple.Bookstore.Book;

public interface BookPreviewProjection {
    Long getId();

    String getTitle();

    String getAuthor();

    String getFrontImage();

    Double getAverageRating();

    String[] getGenres();
}
