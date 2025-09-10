package com.simple.Bookstore.utils;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Comment.CommentReviewViewResponseDTO;
import com.simple.Bookstore.Review.*;
import com.simple.Bookstore.User.User;

import java.util.ArrayList;
import java.util.List;

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
                review.getProfile().getDisplayName(),
                review.getComments()
                        .stream()
                        .map(CommentMapper::commentToReviewViewResponseDTO)
                        .toList()
        );
    }

    public static ReviewResponseDTO projectionToResponseDTO(ReviewProjection projection) {
        List<CommentReviewViewResponseDTO> comments = new ArrayList<>();
        for (int i = 0; i < projection.getCommentIds().length; i++) {
            if (projection.getCommentIds()[i] == null)
                continue;
            comments.add(new CommentReviewViewResponseDTO(
                    projection.getCommentIds()[i],
                    projection.getCommentContents()[i],
                    projection.getCommentDates()[i].toLocalDateTime(),
                    projection.getCommentEdited()[i],
                    projection.getCommentUsernames()[i],
                    projection.getCommentUserDisplayNames()[i]
            ));
        }
        return new ReviewResponseDTO(
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
                comments
        );
    }

    public static ReviewProfileViewResponseDTO viewProjectionToViewResponseDTO(ReviewProfileViewProjection projection) {
        return new ReviewProfileViewResponseDTO(
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
