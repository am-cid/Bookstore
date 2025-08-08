package com.simple.Bookstore.Comment;

import com.simple.Bookstore.Auth.SecurityService;
import com.simple.Bookstore.Exceptions.CommentNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final SecurityService securityService;

    @Override
    public List<CommentResponseDTO> findAllCommentsByReviewId(Long reviewId) {
        return commentRepository
                .findAllCommentsByReviewId(reviewId)
                .stream()
                .map(this::commentToResponseDTO)
                .toList();
    }

    @Override
    public CommentResponseDTO findCommentById(Long id) {
        return commentRepository
                .findById(id)
                .map(this::commentToResponseDTO)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Override
    public CommentResponseDTO createComment(Long reviewId, CommentRequestDTO request) {
        Comment comment = requestDtoToComment(reviewId, request);
        Comment savedComment = commentRepository.save(comment);
        return commentToResponseDTO(savedComment);
    }

    @Override
    public CommentResponseDTO updateComment(Long id, CommentRequestDTO request) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        if (!comment.getUser().getId().equals(securityService.getLoggedInUser().getId()))
            throw new UnauthorizedException("You are not authorized to edit this comment");

        comment.setContent(request.content());
        comment.setEdited(true);
        Comment savedComment = commentRepository.save(comment);
        return commentToResponseDTO(savedComment);
    }

    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        if (!comment.getUser().getId().equals(securityService.getLoggedInUser().getId()))
            throw new UnauthorizedException("You are not authorized to edit this comment");

        commentRepository.deleteById(id);
    }

    private CommentResponseDTO commentToResponseDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getDisplayName(),
                comment.getDate(),
                comment.getContent()
        );
    }

    private Comment requestDtoToComment(Long reviewId, CommentRequestDTO request) {
        Comment comment = new Comment();
        comment.setUser(securityService.getLoggedInUser());
        comment.setReview(reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId)));
        comment.setContent(request.content());
        return comment;

    }
}
