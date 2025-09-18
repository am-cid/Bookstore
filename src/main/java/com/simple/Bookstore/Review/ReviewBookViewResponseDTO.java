package com.simple.Bookstore.Review;

import com.simple.Bookstore.Comment.CommentReviewViewResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * reviews to be rendered on the book view. This means it does not need any of
 * the book information since the book will be requested alongside this.
 * <p>
 * note that comments are included because this is supposed to be used as a
 * preview of the full review thread with all the comments. the comments should
 * be limited since it's a preview(2)
 * <p>
 * these reviews should be rendered under: <code>/books/{bookId}</code>
 *
 * @param id              review id
 * @param title           review title
 * @param content         review content
 * @param rating          review rating
 * @param date            review posted date
 * @param edited          whether review was edited
 * @param username        review user's username
 * @param userDisplayName review user's display name
 * @param comments        list of review comments
 * @param commentCount    total number of comments made by public profiles
 */
public record ReviewBookViewResponseDTO(
        Long id,
        String title,
        String content,
        Integer rating,
        LocalDateTime date,
        boolean edited,
        String username,
        String userDisplayName,
        List<CommentReviewViewResponseDTO> comments,
        Integer commentCount
) {
}
