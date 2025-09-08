package com.simple.Bookstore.utils;

import com.simple.Bookstore.Comment.*;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Review.Review;

public class CommentMapper {

    public static CommentResponseDTO commentToResponseDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getDate(),
                comment.isEdited(),
                comment.getReview().getId(),
                comment.getReview().getTitle(),
                comment.getReview().getProfile().getUser().getUsername(),
                comment.getReview().getProfile().getDisplayName(),
                comment.getReview().getBook().getId(),
                comment.getProfile().getUser().getUsername(),
                comment.getProfile().getDisplayName()
        );
    }

    public static CommentViewResponseDTO viewProjectionToViewResponseDTO(CommentViewProjection projection) {
        return new CommentViewResponseDTO(
                projection.getId(),
                projection.getContent(),
                projection.getDate(),
                projection.getEdited(),
                projection.getReviewId(),
                projection.getReviewTitle(),
                projection.getReviewerUsername(),
                projection.getReviewerDisplayName(),
                projection.getBookId(),
                projection.getUsername(),
                projection.getUserDisplayName(),
                projection.getPageNumber()
        );
    }

    public static Comment requestDtoToComment(Profile profile, Review review, CommentRequestDTO request) {
        Comment comment = new Comment();
        comment.setProfile(profile);
        comment.setReview(review);
        comment.setContent(request.content());
        return comment;

    }
}
