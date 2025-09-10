package com.simple.Bookstore.Comment;

import java.time.LocalDateTime;

public interface CommentProfileViewProjection {
    Long getId();

    String getContent();

    LocalDateTime getDate();

    boolean getEdited();

    Long getReviewId();

    String getReviewTitle();

    String getReviewerUsername();

    String getReviewerDisplayName();

    Long getBookId();

    String getUsername();

    String getUserDisplayName();

    Integer getPageNumber();
}
