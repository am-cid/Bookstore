package com.simple.Bookstore.Review;

import java.time.LocalDateTime;

public interface ReviewProjection {
    Long getId();

    String getTitle();

    String getContent();

    Integer getRating();

    LocalDateTime getDate();

    boolean getEdited();

    Long getBookId();

    String getBookTitle();

    String getBookAuthor();

    String getBookFrontImage();

    String getUsername();

    String getUserDisplayName();

}
