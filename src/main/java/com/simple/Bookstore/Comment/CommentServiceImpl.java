package com.simple.Bookstore.Comment;

import com.simple.Bookstore.Exceptions.CommentNotFoundException;
import com.simple.Bookstore.Exceptions.ReviewNotFoundException;
import com.simple.Bookstore.Exceptions.UnauthorizedException;
import com.simple.Bookstore.Review.Review;
import com.simple.Bookstore.Review.ReviewRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.utils.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public List<CommentResponseDTO> findAllCommentsByReviewId(Long reviewId) {
        return commentRepository
                .findAllCommentsByReviewId(reviewId)
                .stream()
                .map(CommentMapper::commentToResponseDTO)
                .toList();
    }

    @Override
    public CommentResponseDTO findCommentById(Long id) {
        return commentRepository
                .findById(id)
                .map(CommentMapper::commentToResponseDTO)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Override
    public CommentResponseDTO createComment(User user, Long reviewId, CommentRequestDTO request) {
        Review review = reviewRepository
                .findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        Comment comment = CommentMapper.requestDtoToComment(user.getProfile(), review, request);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.commentToResponseDTO(savedComment);
    }

    @Override
    public CommentResponseDTO updateComment(User user, Long id, CommentRequestDTO request) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        if (!comment.getProfile().getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("You are not authorized to edit this comment");

        comment.setContent(request.content());
        comment.setEdited(true);
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.commentToResponseDTO(savedComment);
    }

    @Override
    public void deleteComment(User user, Long id) {
        Comment comment = commentRepository
                .findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        if (!comment.getProfile().getUser().getId().equals(user.getId()))
            throw new UnauthorizedException("You are not authorized to edit this comment");

        commentRepository.deleteById(id);
    }

}
