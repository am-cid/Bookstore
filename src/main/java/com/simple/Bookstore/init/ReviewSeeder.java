package com.simple.Bookstore.init;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
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
@Order(3)
public class ReviewSeeder implements CommandLineRunner {

    private final ReviewRepository reviewRepo;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void run(String... args) throws Exception {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;
        if (reviewRepo.count() > 0)
            return;

        List<Book> books = bookRepo.findAll();
        List<User> users = userRepo.findAll();
        if (books.isEmpty() || users.isEmpty()) {
            System.out.println("Warning: Cannot seed reviews. Book or User entities not found.");
            return;
        }

        Review review1 = new Review();
        review1.setBook(books.get(0));
        review1.setUser(users.get(0));
        review1.setRating(4.5);
        review1.setContent("A magical and enchanting start to a classic series. Highly recommend!");
        review1.setDate(LocalDateTime.now());
        review1.setEdited(false);

        Review review2 = new Review();
        review2.setBook(books.get(0));
        review2.setUser(users.get(1));
        review2.setRating(5.0);
        review2.setContent("The best book I've ever read. A true masterpiece of fantasy.");
        review2.setDate(LocalDateTime.now());
        review2.setEdited(false);

        Review review3 = new Review();
        review3.setBook(books.get(1));
        review3.setUser(users.get(2));
        review3.setRating(5.0);
        review3.setContent("An epic adventure with rich world-building. Tolkien's genius shines through.");
        review3.setDate(LocalDateTime.now());
        review3.setEdited(false);

        reviewRepo.saveAll(List.of(review1, review2, review3));
        System.out.println("Seeded " + reviewRepo.count() + " reviews.");
    }
}
