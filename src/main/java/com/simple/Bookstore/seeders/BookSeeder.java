package com.simple.Bookstore.seeders;

import com.simple.Bookstore.Book.Book;
import com.simple.Bookstore.Book.BookRepository;
import com.simple.Bookstore.Genre.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(2)
public class BookSeeder implements CommandLineRunner {
    private final BookRepository bookRepo;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void run(String... args) throws Exception {
        activeProfile = (activeProfile == null || activeProfile.isBlank()) ? "dev" : activeProfile;
        if (!activeProfile.equals("dev"))
            return;
        if (bookRepo.count() > 0)
            return;

        Book book1 = new Book();
        book1.setTitle("Harry Potter and the Philosopher's Stone");
        book1.setAuthor("J.K. Rowling");
        book1.setDescription("The first book of the Harry Potter series...");
        book1.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book2 = new Book();
        book2.setTitle("The Lord of the Rings");
        book2.setAuthor("J.R.R. Tolkien");
        book2.setDescription("An epic fantasy novel...");
        book2.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book3 = new Book();
        book3.setTitle("Becoming");
        book3.setAuthor("Michelle Obama");
        book3.setDescription("A deeply personal memoir by the former First Lady of the United States.");
        book3.setGenres(Set.of(Genre.AUTOBIOGRAPHY_MEMOIR, Genre.BIOGRAPHY));

        Book book4 = new Book();
        book4.setTitle("1984");
        book4.setAuthor("George Orwell");
        book4.setDescription("A dystopian classic exploring surveillance, truth, and authoritarianism.");
        book4.setGenres(Set.of(Genre.CLASSIC_FICTION, Genre.SCIENCE_FICTION));

        Book book5 = new Book();
        book5.setTitle("The Silent Patient");
        book5.setAuthor("Alex Michaelides");
        book5.setDescription("A gripping psychological thriller about a woman who stops speaking after a shocking crime.");
        book5.setGenres(Set.of(Genre.PSYCHOLOGICAL_THRILLER, Genre.MYSTERY));

        Book book6 = new Book();
        book6.setTitle("The Subtle Art of Not Giving a F*ck");
        book6.setAuthor("Mark Manson");
        book6.setDescription("A counterintuitive approach to living a good life.");
        book6.setGenres(Set.of(Genre.SELF_HELP, Genre.PHILOSOPHY));

        Book book7 = new Book();
        book7.setTitle("A Court of Thorns and Roses");
        book7.setAuthor("Sarah J. Maas");
        book7.setDescription("A romantic fantasy about fae, power, and betrayal.");
        book7.setGenres(Set.of(Genre.FANTASY_ROMANCE_ROMANTASY, Genre.DARK_FANTASY));

        Book book8 = new Book();
        book8.setTitle("Goodnight Moon");
        book8.setAuthor("Margaret Wise Brown");
        book8.setDescription("A beloved children's bedtime classic.");
        book8.setGenres(Set.of(Genre.CHILDRENS_FICTION));

        Book book9 = new Book();
        book9.setTitle("The Martian");
        book9.setAuthor("Andy Weir");
        book9.setDescription("A stranded astronaut fights to survive on Mars.");
        book9.setGenres(Set.of(Genre.HARD_SCI_FI, Genre.SCIENCE_FICTION));

        Book book10 = new Book();
        book10.setTitle("Kitchen Confidential");
        book10.setAuthor("Anthony Bourdain");
        book10.setDescription("Behind-the-scenes stories from the world of professional kitchens.");
        book10.setGenres(Set.of(Genre.FOOD_DRINK, Genre.BIOGRAPHY));

        Book book11 = new Book();
        book11.setTitle("Pride and Prejudice");
        book11.setAuthor("Jane Austen");
        book11.setDescription("A romantic classic exploring love, class, and manners.");
        book11.setGenres(Set.of(
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
                Genre.BIOGRAPHY
        ));

        Book book12 = new Book();
        book12.setTitle("The Haunting of Hill House");
        book12.setAuthor("Shirley Jackson");
        book12.setDescription("A chilling gothic horror story in a haunted mansion.");
        book12.setGenres(Set.of(Genre.GOTHIC_HORROR, Genre.PSYCHOLOGICAL_HORROR));

        Book book13 = new Book();
        book13.setTitle("Harry Potter and the Chamber of Secrets");
        book13.setAuthor("J.K. Rowling");
        book13.setDescription("The second book in the series, where a dark secret is uncovered at Hogwarts.");
        book13.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book14 = new Book();
        book14.setTitle("Harry Potter and the Prisoner of Azkaban");
        book14.setAuthor("J.K. Rowling");
        book14.setDescription("The third book, introducing Sirius Black and the Dementors.");
        book14.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book15 = new Book();
        book15.setTitle("Harry Potter and the Goblet of Fire");
        book15.setAuthor("J.K. Rowling");
        book15.setDescription("The fourth book, featuring the Triwizard Tournament and the return of Voldemort.");
        book15.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book16 = new Book();
        book16.setTitle("Harry Potter and the Order of the Phoenix");
        book16.setAuthor("J.K. Rowling");
        book16.setDescription("The fifth book, with Harry's fifth year at Hogwarts and the formation of Dumbledore's Army.");
        book16.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book17 = new Book();
        book17.setTitle("Harry Potter and the Half-Blood Prince");
        book17.setAuthor("J.K. Rowling");
        book17.setDescription("The sixth book, exploring Voldemort's past and a tragic loss.");
        book17.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book18 = new Book();
        book18.setTitle("Harry Potter and the Deathly Hallows");
        book18.setAuthor("J.K. Rowling");
        book18.setDescription("The final book, as Harry, Ron, and Hermione hunt for Horcruxes and face the final battle.");
        book18.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book19 = new Book();
        book19.setTitle("Fantastic Beasts and Where to Find Them");
        book19.setAuthor("J.K. Rowling");
        book19.setDescription("A magical textbook from the wizarding world.");
        book19.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book20 = new Book();
        book20.setTitle("Fantastic Beasts: The Crimes of Grindelwald");
        book20.setAuthor("J.K. Rowling");
        book20.setDescription("A screenplay from the second movie in the Fantastic Beasts series.");
        book20.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book21 = new Book();
        book21.setTitle("Harry Potter and the Cursed Child");
        book21.setAuthor("J.K. Rowling");
        book21.setDescription("The official eighth story in the Harry Potter series, a new play.");
        book21.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book22 = new Book();
        book22.setTitle("Quidditch Through the Ages");
        book22.setAuthor("J.K. Rowling");
        book22.setDescription("A history of the wizarding world's most popular sport.");
        book22.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        Book book23 = new Book();
        book23.setTitle("The Tales of Beedle the Bard");
        book23.setAuthor("J.K. Rowling");
        book23.setDescription("A collection of fairy tales for young wizards and witches.");
        book23.setGenres(Set.of(Genre.FANTASY, Genre.ACTION_ADVENTURE_FICTION));

        bookRepo.saveAll(List.of(
                book1, book2, book3, book4, book5, book6, book7, book8, book9,
                book10, book11, book12, book13, book14, book15, book16, book17,
                book18, book19, book20, book21, book22, book23
        ));
    }
}
