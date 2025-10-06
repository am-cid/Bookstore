package com.simple.Bookstore.Comment;

import com.simple.Bookstore.Exceptions.CommentNotFoundException;
import com.simple.Bookstore.Exceptions.ForbiddenException;
import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    List<CommentResponseDTO> findAllCommentsByReviewId(Long reviewId);

    CommentResponseDTO findCommentById(Long id);

    CommentResponseDTO createComment(User user, Long reviewId, CommentRequestDTO request);

    CommentResponseDTO updateComment(
            User user,
            Long id,
            CommentRequestDTO request
    ) throws CommentNotFoundException, ForbiddenException;

    void deleteComment(User user, Long id) throws CommentNotFoundException, ForbiddenException;

    Page<CommentProfileViewResponseDTO> findAllCommentsByUser(User user, Pageable pageable);

    Page<CommentReviewViewResponseDTO> findAllPublicOrOwnedCommentsByReviewIdAsPage(Long reviewId, User user, Pageable pageable);

    Integer countAllPublicOrOwnedCommentsByReviewId(Long reviewId, User user);
}
