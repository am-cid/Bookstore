package com.simple.Bookstore.Comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/reviews/{reviewId}/comments")
    private ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable Long reviewId) {
        return new ResponseEntity<>(
                commentService.findAllCommentsByReviewId(reviewId),
                HttpStatus.OK
        );
    }

    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long reviewId,
            @RequestBody CommentRequestDTO request
    ) {
        return new ResponseEntity<>(
                commentService.createComment(reviewId, request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDTO> getReview(@PathVariable Long id) {
        return new ResponseEntity<>(
                commentService.findCommentById(id),
                HttpStatus.OK
        );
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDTO> updateReview(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO request
    ) {
        CommentResponseDTO updatedComment = commentService.updateComment(id, request);
        return new ResponseEntity<>(
                updatedComment,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        commentService.deleteComment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
