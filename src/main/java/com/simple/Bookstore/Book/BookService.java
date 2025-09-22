package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    List<BookSearchResultDTO> findAllBooks();

    List<BookPreviewDTO> findLatestNBooks(int n);

    List<BookSearchResultDTO> findTopNRatedBooks(int n);

    List<BookSearchResultDTO> findRelevantBooks(int limit);

    List<BookSearchResultDTO> findSavedBooks(User user);

    List<Long> findSavedBookIds(User user);

    Page<BookSearchResultDTO> findSavedBooks(User user, Pageable pageable);

    Optional<BookSearchResultDTO> findBookById(Long id);

    List<String> findDistinctAuthors();

    Book createBook(BookRequestDTO bookDTO);

    Book updateBook(Long id, BookRequestDTO book);

    void deleteBook(Long id);

    Page<BookSearchResultDTO> searchBooks(String query, Optional<Set<Genre>> genres, Double rating, Pageable pageable);
}
