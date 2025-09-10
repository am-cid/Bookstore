package com.simple.Bookstore.Comment;

import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    List<CommentResponseDTO> findAllCommentsByReviewId(Long reviewId);

    CommentResponseDTO findCommentById(Long id);

    CommentResponseDTO createComment(User user, Long reviewId, CommentRequestDTO request);

    CommentResponseDTO updateComment(User user, Long id, CommentRequestDTO request);

    void deleteComment(User user, Long id);

    Page<CommentProfileViewResponseDTO> findAllCommentsByUser(User user, Pageable pageable);
}
