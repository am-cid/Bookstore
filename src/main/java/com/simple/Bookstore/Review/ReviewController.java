package com.simple.Bookstore.Review;

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
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponseDTO>> getReviews(@PathVariable Long bookId) {
        return new ResponseEntity<>(
                reviewService.findAllPublicOrOwnedReviewsByBookId(bookId),
                HttpStatus.OK
        );
    }

    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponseDTO> createReview(
            @PathVariable Long bookId,
            @RequestBody ReviewRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        return new ResponseEntity<>(
                reviewService.createReview(user, bookId, request),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponseDTO> getReview(@PathVariable Long id) {
        return new ResponseEntity<>(
                reviewService.findReviewById(id),
                HttpStatus.OK
        );
    }


    @PatchMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewRequestDTO request,
            @AuthenticationPrincipal User user
    ) {
        ReviewResponseDTO updatedReview = reviewService.updateReview(user, id, request);
        return new ResponseEntity<>(
                updatedReview,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        reviewService.deleteReview(user, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
