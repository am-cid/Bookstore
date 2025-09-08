package com.simple.Bookstore.utils;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Review.*;
import com.simple.Bookstore.User.User;

public class ReviewMapper {
    public static ReviewResponseDTO reviewToResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getTitle(),
                review.getContent(),
                review.getRating(),
                review.getDate(),
                review.isEdited(),
                review.getBook().getId(),
                review.getBook().getTitle(),
                review.getBook().getAuthor(),
                review.getBook().getFrontImage(),
                review.getProfile().getUser().getUsername(),
                review.getProfile().getDisplayName()
        );
    }

    public static ReviewViewResponseDTO viewProjectionToViewResponseDTO(ReviewViewProjection projection) {
        return new ReviewViewResponseDTO(
                projection.getId(),
                projection.getTitle(),
                projection.getContent(),
                projection.getRating(),
                projection.getDate(),
                projection.getEdited(),
                projection.getBookId(),
                projection.getBookTitle(),
                projection.getBookAuthor(),
                projection.getBookFrontImage(),
                projection.getUsername(),
                projection.getUserDisplayName(),
                projection.getPageNumber()
        );
    }

    public static Review requestDtoToReview(User user, Book book, ReviewRequestDTO request) {
        Review review = new Review();
        review.setProfile(user.getProfile());
        review.setBook(book);
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setContent(request.content());
        return review;
    }

}
