package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
import com.simple.Bookstore.Review.Review;
import com.simple.Bookstore.Review.ReviewRepository;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
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
            log.warn("Cannot seed reviews. Book or User entities not found.");
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

        Review review4 = new Review();
        review4.setBook(books.get(12));
        review4.setUser(users.get(0));
        review4.setRating(4.0);
        review4.setContent("A fun follow-up with a great mystery. The flying car scene is iconic!");
        review4.setDate(LocalDateTime.now());
        review4.setEdited(false);
        reviewRepo.save(review4);

        Review review5 = new Review();
        review5.setBook(books.get(13));
        review5.setUser(users.get(1));
        review5.setRating(5.0);
        review5.setContent("The best one in the series! The plot twist is incredible and the introduction of Sirius Black is perfect.");
        review5.setDate(LocalDateTime.now());
        review5.setEdited(false);
        reviewRepo.save(review5);

        Review review6 = new Review();
        review6.setBook(books.get(14));
        review6.setUser(users.get(2));
        review6.setRating(4.5);
        review6.setContent("A much darker turn for the series. The Triwizard Tournament is thrilling, but the ending is heartbreaking.");
        review6.setDate(LocalDateTime.now());
        review6.setEdited(false);
        reviewRepo.save(review6);

        Review review7 = new Review();
        review7.setBook(books.get(15));
        review7.setUser(users.get(0));
        review7.setRating(3.5);
        review7.setContent("It's a bit long and Harry's constantly angry, but the development of Dumbledore's Army is a highlight.");
        review7.setDate(LocalDateTime.now());
        review7.setEdited(false);
        reviewRepo.save(review7);

        Review review8 = new Review();
        review8.setBook(books.get(16));
        review8.setUser(users.get(1));
        review8.setRating(4.5);
        review8.setContent("A fantastic book that delves deep into Voldemort's past. The ending is incredibly sad and impactful.");
        review8.setDate(LocalDateTime.now());
        review8.setEdited(false);
        reviewRepo.save(review8);

        Review review9 = new Review();
        review9.setBook(books.get(17));
        review9.setUser(users.get(2));
        review9.setRating(5.0);
        review9.setContent("The perfect finale to an amazing series. It ties everything together beautifully.");
        review9.setDate(LocalDateTime.now());
        review9.setEdited(false);
        reviewRepo.save(review9);

        reviewRepo.saveAll(List.of(
                review1, review2, review3, review4, review5, review6, review7,
                review8, review9
        ));
        log.info("Seeded {} reviews.", reviewRepo.count());
    }
}
