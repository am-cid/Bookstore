package com.simple.Bookstore.Review;

import java.time.LocalDateTime;

/**
 * reviews to be rendered on the book's review view. This means it does needs
 * the book information since the book will not be requested alongside this.
 * <p>
 * note that the book id is left out because it will be a path parameter.
 * better to pass it in the model as a separate value
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
        String bookTitle,
        String bookAuthor,
        String bookFrontImage,
        String username,
        String userDisplayName
) {
}
