package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
import com.simple.Bookstore.Genre.Genre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j(topic = "BookSeeder")
@Component
@RequiredArgsConstructor
@Order(2)
public class BookSeeder implements CommandLineRunner {
    private final BookRepository bookRepo;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private static Book createBook(String title, String author, String description, Set<Genre> genres) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setGenres(genres);

        log.info("Seeded new book: {}", title);
        return book;
    }

    @Override
    public void run(String... args) throws Exception {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;
        if (bookRepo.count() > 0)
            return;
        List<Book> books = List.of(
                createBook(
                        "Harry Potter and the Philosopher's Stone",
                        "J.K. Rowling",
                        "The first book of the Harry Potter series...",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Harry Potter and the Chamber of Secrets",
                        "J.K. Rowling",
                        "The second book in the series, where a dark secret is uncovered at Hogwarts.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Harry Potter and the Prisoner of Azkaban",
                        "J.K. Rowling",
                        "The third book, introducing Sirius Black and the Dementors.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Harry Potter and the Goblet of Fire",
                        "J.K. Rowling",
                        "The fourth book, featuring the Triwizard Tournament and the return of Voldemort.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Harry Potter and the Order of the Phoenix",
                        "J.K. Rowling",
                        "The fifth book, with Harry's fifth year at Hogwarts and the formation of Dumbledore's Army.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Harry Potter and the Half-Blood Prince",
                        "J.K. Rowling",
                        "The sixth book, exploring Voldemort's past and a tragic loss.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Harry Potter and the Deathly Hallows",
                        "J.K. Rowling",
                        "The final book, as Harry, Ron, and Hermione hunt for Horcruxes and face the final battle.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Fantastic Beasts and Where to Find Them",
                        "J.K. Rowling",
                        "A magical textbook from the wizarding world.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Fantastic Beasts: The Crimes of Grindelwald",
                        "J.K. Rowling",
                        "A screenplay from the second movie in the Fantastic Beasts series.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Harry Potter and the Cursed Child",
                        "J.K. Rowling",
                        "The official eighth story in the Harry Potter series, a new play.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Quidditch Through the Ages",
                        "J.K. Rowling",
                        "A history of the wizarding world's most popular sport.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "The Tales of Beedle the Bard",
                        "J.K. Rowling",
                        "A collection of fairy tales for young wizards and witches.",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "The Lord of the Rings",
                        "J.R.R. Tolkien",
                        "An epic fantasy novel...",
                        Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION)
                ),
                createBook(
                        "Becoming",
                        "Michelle Obama",
                        "A deeply personal memoir by the former First Lady of the United States.",
                        Set.of(Genre.AUTOBIOGRAPHY_MEMOIR, Genre.BIOGRAPHY)
                ),
                createBook(
                        "1984",
                        "George Orwell",
                        "A dystopian classic exploring surveillance, truth, and authoritarianism.",
                        Set.of(Genre.CLASSIC_FICTION, Genre.SCIENCE_FICTION)
                ),
                createBook(
                        "The Silent Patient",
                        "Alex Michaelides",
                        "A gripping psychological thriller about a woman who stops speaking after a shocking crime.",
                        Set.of(Genre.PSYCHOLOGICAL_THRILLER, Genre.MYSTERY)
                ),
                createBook(
                        "The Subtle Art of Not Giving a F*ck",
                        "Mark Manson",
                        "A counterintuitive approach to living a good life.",
                        Set.of(Genre.SELF_HELP, Genre.PHILOSOPHY)
                ),
                createBook(
                        "A Court of Thorns and Roses",
                        "Sarah J. Maas",
                        "A romantic fantasy about fae, power, and betrayal.",
                        Set.of(Genre.FANTASY_ROMANCE_ROMANTASY, Genre.DARK_FANTASY)
                ),
                createBook(
                        "Goodnight Moon",
                        "Margaret Wise Brown",
                        "A beloved children's bedtime classic.",
                        Set.of(Genre.CHILDRENS_FICTION)
                ),
                createBook(
                        "The Martian",
                        "Andy Weir",
                        "A stranded astronaut fights to survive on Mars.",
                        Set.of(Genre.HARD_SCI_FI, Genre.SCIENCE_FICTION)
                ),
                createBook(
                        "Kitchen Confidential",
                        "Anthony Bourdain",
                        "Behind-the-scenes stories from the world of professional kitchens.",
                        Set.of(Genre.FOOD_DRINK, Genre.BIOGRAPHY)
                ),
                createBook(
                        "Pride and Prejudice",
                        "Jane Austen",
                        "A romantic classic exploring love, class, and manners.",
                        Set.of(
                                Genre.CLASSIC_FICTION,
                                Genre.ROMANCE,
                                Genre.ACTION_ADVENTURE_FICTION,
                                Genre.MYSTERY,
                                Genre.CHILDRENS_FICTION,
                                Genre.HARD_SCI_FI,
                                Genre.SCIENCE_FICTION,
                                Genre.SATIRE,
                                Genre.CONTEMPORARY_ROMANCE,
                                Genre.DARK_ROMANCE,
                                Genre.BIOGRAPHY)
                ),
                createBook(
                        "The Haunting of Hill House",
                        "Shirley Jackson",
                        "A chilling gothic horror story in a haunted mansion.",
                        Set.of(Genre.GOTHIC_HORROR, Genre.PSYCHOLOGICAL_HORROR)
                )
        );
        log.info("Seeded {} books", books.size());
        bookRepo.saveAll(books);
    }
}
