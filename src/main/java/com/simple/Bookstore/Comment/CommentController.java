package com.simple.Bookstore.Comment;

import com.simple.Bookstore.User.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

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
            @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        return new ResponseEntity<>(
                commentService.createComment(user, reviewId, request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDTO> getComment(@PathVariable Long id) {
        return new ResponseEntity<>(
                commentService.findCommentById(id),
                HttpStatus.OK
        );
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        CommentResponseDTO updatedComment = commentService.updateComment(user, id, request);
        return new ResponseEntity<>(
                updatedComment,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        commentService.deleteComment(user, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
