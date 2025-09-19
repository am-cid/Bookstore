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
import java.util.Random;

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
            Integer rating,
            String title,
            String content
    ) {
        Review review = new Review();
        review.setBook(book);
        review.setProfile(profile);
        review.setRating(rating);
        review.setTitle(title);
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
            log.warn("Cannot seed profileReviews. Book or User entities not found.");
            return;
        }

        for (int i = 0; i < 20; i++) {
            reviewRepo.save(createReview(
                    books.get(6),
                    profiles.get(2),
                    new Random().nextInt(6),
                    "Random Review " + i,
                    "review content for random review " + i
            ));
        }
        reviewRepo.saveAll(List.of(
                createReview(
                        books.get(0),
                        profiles.get(0),
                        4,
                        "A magical and enchanting start to a classic series",
                        "A truly magical and enchanting start to a classic series. J.K. Rowling masterfully introduces the reader to a world of magic, friendship, and wonder. The characters are immediately lovable, and the mystery surrounding the Sorcerer's Stone is compelling. Highly recommend this for anyone, young or old, looking for a sense of adventure and a place to escape."
                ),
                createReview(
                        books.get(12),
                        profiles.get(1),
                        5,
                        "The best book I've ever read",
                        "The best book I've ever read, hands down. Tolkien’s world-building is unparalleled; every detail, from the languages to the lore, feels incredibly real and lived-in. The journey of the Fellowship is a profound tale of courage, sacrifice, and the enduring power of hope. It's a true masterpiece of fantasy that sets the standard for the entire genre."
                ),
                createReview(
                        books.get(12),
                        profiles.get(0),
                        4,
                        "An epic adventure with rich world-building",
                        "An epic adventure with rich world-building that will transport you to Middle-earth. Tolkien’s genius shines through on every page, creating a vast and detailed history that makes the story so much more than a simple quest. The characters are beautifully developed, and the stakes feel incredibly high, making for a truly unforgettable reading experience."
                ),
                createReview(
                        books.get(1),
                        profiles.get(0),
                        4,
                        "A fun follow-up with a great mystery",
                        "A fun follow-up to the first book with a great mystery at its core. The introduction of Dobby and the secrets of the Chamber of Secrets keep the pages turning. The flying car scene is iconic and the basilisk reveal is thrilling. It's a solid, enjoyable read that builds on the foundation of the first book."
                ),
                createReview(
                        books.get(2),
                        profiles.get(1),
                        5,
                        "The best one in the series!",
                        "In my opinion, this is the best one in the series. The plot twist is incredible, and the introduction of Sirius Black and the Dementors adds a much-needed layer of maturity and darkness. It's a perfectly paced, emotional, and complex story that shows how much the characters have grown. The narrative tension is at its peak here."
                ),
                createReview(
                        books.get(3),
                        profiles.get(2),
                        4,
                        "A much darker turn for the series",
                        "A much darker and more intense turn for the series. The Triwizard Tournament is a thrilling and well-crafted challenge, pushing the characters to their limits. However, the ending is absolutely heartbreaking and changes the entire direction of the series. This book proves that the stakes are higher than ever and the innocence of childhood is fading fast."
                ),
                createReview(
                        books.get(4),
                        profiles.get(0),
                        3,
                        "Harry's constantly angry, but Dumbledore's Army...",
                        "While it's a bit long and Harry's constantly angry throughout, the development of Dumbledore's Army and the resistance against the Ministry of Magic is a major highlight. The book effectively captures the frustrations of teenage rebellion and political corruption, and the bond between the trio is tested and strengthened. A crucial stepping stone in the overall plot."
                ),
                createReview(
                        books.get(5),
                        profiles.get(1),
                        4,
                        "A fantastic book that delves deep into Voldemort's past",
                        "A fantastic book that delves deep into Voldemort's past, providing crucial context for the final confrontation. The mystery of the Half-Blood Prince is intriguing, and the romantic subplots add a new dynamic. The ending is incredibly sad and impactful, leaving the reader with a sense of immense loss and a feeling of foreboding for the final installment."
                ),
                createReview(
                        books.get(6),
                        profiles.get(0),
                        5,
                        "The perfect finale to an amazing series",
                        "The perfect finale to an amazing series. It ties everything together beautifully, providing a satisfying conclusion to the central conflict and the journeys of the main characters. The action is relentless, the emotional moments are powerful, and the themes of love and sacrifice are brought to a moving climax. A fitting and powerful end to a literary phenomenon."
                ),
                createReview(
                        books.get(6),
                        profiles.get(1),
                        1,
                        "huh?",
                        "voldemort is so stupid man"
                ),
                createReview(
                        books.get(21),
                        profiles.get(0),
                        5,
                        "WOWIE!",
                        "This literally has all the genres you'd possibly want, all in one book. It's a chaotic, hilarious, and heartwarming ride that never lets up. The author blends sci-fi, fantasy, comedy, and drama so seamlessly that you can't help but be fully immersed. It’s a completely unique experience that I can't recommend enough."
                )
        ));
        log.info("Seeded {} profileReviews.", reviewRepo.count());
    }
}
