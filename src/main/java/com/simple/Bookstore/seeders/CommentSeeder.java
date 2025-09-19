package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Comment.Comment;
import com.simple.Bookstore.Comment.CommentRepository;
import com.simple.Bookstore.Profile.Profile;
import com.simple.Bookstore.Profile.ProfileRepository;
import com.simple.Bookstore.Review.Review;
import com.simple.Bookstore.Review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(4)
public class CommentSeeder implements CommandLineRunner {

    private final CommentRepository commentRepo;
    private final ReviewRepository reviewRepo;
    private final ProfileRepository profileRepo;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private static Comment createComment(
            Review review,
            Profile profile,
            String content
    ) {
        Comment comment = new Comment();
        comment.setReview(review);
        comment.setProfile(profile);
        comment.setContent(content);
        comment.setDate(LocalDateTime.now());
        comment.setEdited(false);
        log.info("Seeded new comment for {}", review.getTitle());
        return comment;
    }

    @Override
    public void run(String... args) throws Exception {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;
        if (commentRepo.count() > 0)
            return;

        List<Review> reviews = reviewRepo.findAll();
        List<Profile> profiles = profileRepo.findAll();

        if (reviews.isEmpty() || profiles.isEmpty()) {
            log.warn("Cannot seed profileComments. Review or User entities not found.");
            return;
        }

        commentRepo.saveAll(List.of(
                createComment(
                        reviews.get(20),
                        profiles.get(1),
                        "I completely agree! The world-building is fantastic."
                ),
                createComment(
                        reviews.get(21),
                        profiles.get(0),
                        "Couldn't have said it better myself. Tolkien is a genius."
                ),
                createComment(
                        reviews.get(21),
                        profiles.get(2),
                        "Amazing book, glad you enjoyed it!"
                ),
                createComment(
                        reviews.get(21),
                        profiles.get(1),
                        "wowowowowow!"
                ),
                createComment(
                        reviews.get(22),
                        profiles.get(1),
                        "Stop the yappachino!"
                )
        ));
        for (int i = 0; i < 20; i++) {
            commentRepo.save(createComment(reviews.get(1), profiles.get(2), String.valueOf(i + 1)));
        }
        log.info("Seeded {} profileComments.", commentRepo.count());
    }
}
