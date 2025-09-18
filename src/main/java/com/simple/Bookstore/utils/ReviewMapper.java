package com.simple.Bookstore.utils;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Comment.CommentReviewViewResponseDTO;
import com.simple.Bookstore.Review.*;
import com.simple.Bookstore.User.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReviewMapper {
    public static Review requestDtoToReview(User user, Book book, ReviewRequestDTO request) {
        Review review = new Review();
        review.setProfile(user.getProfile());
        review.setBook(book);
        review.setRating(request.rating());
        review.setTitle(request.title() == null || request.title().isEmpty() ? book.getTitle() + " Review" : request.title());
        review.setContent(request.content());
        return review;
    }

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
                review.getComments() == null
                        ? null
                        : review.getComments()
                        .stream()
                        .map(CommentMapper::commentToReviewViewResponseDTO)
                        .toList(),
                review.getComments() == null ? 0 : review.getComments().size()
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

    public static ReviewBookViewResponseDTO bookViewProjectionToBookViewResponseDTO(ReviewBookViewProjection projection) {
        return new ReviewBookViewResponseDTO(
                projection.getId(),
                projection.getTitle(),
                projection.getContent(),
                projection.getRating(),
                projection.getDate(),
                projection.getEdited(),
                projection.getUsername(),
                projection.getUserDisplayName(),
                aggregateComments(
                        projection.getCommentIds(),
                        projection.getCommentContents(),
                        projection.getCommentDates(),
                        projection.getCommentEdited(),
                        projection.getCommentUsernames(),
                        projection.getCommentUserDisplayNames()
                ),
                projection.getCommentCount()
        );
    }

    private static List<CommentReviewViewResponseDTO> aggregateComments(
            Long[] ids,
            String[] contents,
            Timestamp[] dates,
            Boolean[] edited,
            String[] usernames,
            String[] displayNames
    ) {
        List<CommentReviewViewResponseDTO> comments = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == null)
                continue;
            comments.add(new CommentReviewViewResponseDTO(
                    ids[i],
                    contents[i],
                    dates[i].toLocalDateTime(),
                    edited[i],
                    usernames[i],
                    displayNames[i]
            ));
        }
        return comments;
    }
}
