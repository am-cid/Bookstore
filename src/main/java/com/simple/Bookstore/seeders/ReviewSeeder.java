package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(3)
public class ReviewSeeder implements CommandLineRunner {

    private final ReviewRepository reviewRepo;
    private final BookRepository bookRepo;
    private final ProfileRepository profileRepo;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private static Review createReview(
            Book book,
            Profile profile,
            Double rating,
            String content
    ) {
        Review review = new Review();
        review.setBook(book);
        review.setProfile(profile);
        review.setRating(4.5);
        review.setContent(content);
        review.setEdited(false);
        log.info("Seeded new review for {}", book.getTitle());
        return review;
    }

    @Override
    public void run(String... args) throws Exception {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;
        if (reviewRepo.count() > 0)
            return;

        List<Book> books = bookRepo.findAll();
        List<Profile> profiles = profileRepo.findAll();
        if (books.isEmpty() || profiles.isEmpty()) {
            log.warn("Cannot seed reviews. Book or User entities not found.");
            return;
        }

        reviewRepo.saveAll(List.of(
                createReview(
                        books.get(0),
                        profiles.get(0),
                        4.5,
                        "A magical and enchanting start to a classic series. Highly recommend!"
                ),
                createReview(
                        books.get(12),
                        profiles.get(1),
                        5.0,
                        "The best book I've ever read. A true masterpiece of fantasy."
                ),
                createReview(
                        books.get(12),
                        profiles.get(2),
                        5.0,
                        "An epic adventure with rich world-building. Tolkien's genius shines through."
                ),
                createReview(
                        books.get(1),
                        profiles.get(0),
                        4.0,
                        "A fun follow-up with a great mystery. The flying car scene is iconic!"
                ),
                createReview(
                        books.get(2),
                        profiles.get(1),
                        5.0,
                        "The best one in the series! The plot twist is incredible and the introduction of Sirius Black is perfect."
                ),
                createReview(
                        books.get(3),
                        profiles.get(2),
                        4.5,
                        "A much darker turn for the series. The Triwizard Tournament is thrilling, but the ending is heartbreaking."
                ),
                createReview(
                        books.get(4),
                        profiles.get(0),
                        3.5,
                        "It's a bit long and Harry's constantly angry, but the development of Dumbledore's Army is a highlight."
                ),
                createReview(
                        books.get(5),
                        profiles.get(1),
                        4.5,
                        "A fantastic book that delves deep into Voldemort's past. The ending is incredibly sad and impactful."
                ),
                createReview(
                        books.get(6),
                        profiles.get(2),
                        5.0,
                        "The perfect finale to an amazing series. It ties everything together beautifully."
                ),
                createReview(
                        books.get(21),
                        profiles.get(0),
                        5.0,
                        "WOW! This literally has all the genres you'd possibly want"
                )
        ));
        log.info("Seeded {} reviews.", reviewRepo.count());
    }
}
