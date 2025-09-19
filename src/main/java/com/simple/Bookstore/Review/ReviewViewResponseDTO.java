package com.simple.Bookstore.Review;

import java.time.LocalDateTime;

/**
 * reviews to be rendered on the book's review view. This means it does need
 * the book information since the book will <strong>NOT</strong> be requested alongside this.
 * <p>
 * these reviews should be rendered under:
 * <code>/books/{bookId}/reviews/{reviewId}</code>
 *
 * @param id              review id
 * @param title           review title
 * @param content         review content
 * @param rating          review rating
 * @param date            review posted date
 * @param edited          whether review was edited
 * @param bookTitle       book title
 * @param bookAuthor      book author
 * @param bookFrontImage  book front image if any
 * @param username        review user's username
 * @param userDisplayName review user's display name
 */
public record ReviewViewResponseDTO(
        Long id,
        String title,
        String content,
        Integer rating,
        LocalDateTime date,
        boolean edited,
        boolean isPublic,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        String bookFrontImage,
        String username,
        String userDisplayName
) {
}
