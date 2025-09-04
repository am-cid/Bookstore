package com.simple.Bookstore.utils;

import com.simple.Bookstore.Comment.Comment;
import com.simple.Bookstore.Comment.CommentRequestDTO;
import com.simple.Bookstore.Comment.CommentResponseDTO;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Review.Review;

public class CommentMapper {

    public static CommentResponseDTO commentToResponseDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getDate(),
                comment.isEdited(),
                comment.getProfile().getUser().getUsername(),
                comment.getProfile().getDisplayName()
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
