package com.simple.Bookstore.utils;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Review.Review;
import com.simple.Bookstore.Review.ReviewRequestDTO;
import com.simple.Bookstore.Review.ReviewResponseDTO;
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
