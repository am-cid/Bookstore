package com.simple.Bookstore.Review;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public interface ReviewBookViewProjection {
    Long getId();

    String getTitle();

    String getContent();

    Integer getRating();

    LocalDateTime getDate();

    Boolean getEdited();

    String getUsername();

    String getUserDisplayName();

    Integer getCommentCount();

    Long[] getCommentIds();

    String[] getCommentContents();

    Timestamp[] getCommentDates();

    Boolean[] getCommentEdited();

    String[] getCommentUsernames();

    String[] getCommentUserDisplayNames();
}
