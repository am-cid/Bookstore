package com.simple.Bookstore.init;

import com.simple.Bookstore.Comment.Comment;
import com.simple.Bookstore.Comment.CommentRepository;
import com.simple.Bookstore.Review.Review;
import com.simple.Bookstore.Review.ReviewRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(4)
public class CommentSeeder implements CommandLineRunner {

    private final CommentRepository commentRepo;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void run(String... args) throws Exception {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;
        if (commentRepo.count() > 0)
            return;

        List<Review> reviews = reviewRepo.findAll();
        List<User> users = userRepo.findAll();

        if (reviews.isEmpty() || users.isEmpty()) {
            System.out.println("Warning: Cannot seed comments. Review or User entities not found.");
            return;
        }

        Comment comment1 = new Comment();
        comment1.setReview(reviews.get(0));
        comment1.setUser(users.get(1));
        comment1.setContent("I completely agree! The world-building is fantastic.");
        comment1.setDate(LocalDateTime.now());
        comment1.setEdited(false);

        Comment comment2 = new Comment();
        comment2.setReview(reviews.get(1));
        comment2.setUser(users.get(0));
        comment2.setContent("Couldn't have said it better myself. Tolkien is a genius.");
        comment2.setDate(LocalDateTime.now());
        comment2.setEdited(false);

        Comment comment3 = new Comment();
        comment3.setReview(reviews.get(1));
        comment3.setUser(users.get(2));
        comment3.setContent("Amazing book, glad you enjoyed it!");
        comment3.setDate(LocalDateTime.now());
        comment3.setEdited(false);

        commentRepo.saveAll(List.of(comment1, comment2, comment3));
        System.out.println("Seeded " + commentRepo.count() + " comments.");
    }
}
