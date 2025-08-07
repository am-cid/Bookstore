package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    List<BookSearchResultDTO> getAllBooks();

    Optional<BookSearchResultDTO> getBookById(Long id);

    Book createBook(BookRequestDTO bookDTO);

    Book updateBook(Long id, BookRequestDTO book);

    void deleteBook(Long id);

    Page<BookSearchResultDTO> searchBooks(String query, Set<Genre> genres, Double rating, Pageable pageable);

    // HELPERS
    Book createDtoToBook(BookRequestDTO dto);

    void updateOldBookWithRequestDTO(Book oldBook, BookRequestDTO bookRequestDTO);
}
