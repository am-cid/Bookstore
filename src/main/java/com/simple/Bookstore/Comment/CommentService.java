package com.simple.Bookstore.Comment;

import java.util.List;

public interface CommentService {
    List<CommentResponseDTO> findAllCommentsByReviewId(Long reviewId);

    CommentResponseDTO findCommentById(Long id);

    CommentResponseDTO createComment(Long reviewId, CommentRequestDTO request);

    CommentResponseDTO updateComment(Long id, CommentRequestDTO request);

    void deleteComment(Long id);
}
