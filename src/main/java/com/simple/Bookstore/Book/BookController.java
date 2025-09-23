package com.simple.Bookstore.Book;

import com.simple.Bookstore.Genre.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookSearchResultDTO>> getBooks() {
        return new ResponseEntity<>(bookService.findAllBooks(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody BookRequestDTO dto) {
        Book newBook = bookService.createBook(dto);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<BookSearchResultDTO>> getBook(@PathVariable Long id) {
        return new ResponseEntity<>(bookService.findBookById(id), HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookRequestDTO bookRequestDTO) {
        Book updatedBook = bookService.updateBook(id, bookRequestDTO);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BookSearchResultDTO>> searchBooks(
            @RequestParam(required = false) Optional<String> query,
            @RequestParam(required = false) Optional<Set<Genre>> genres,
            @RequestParam(required = false) Optional<Double> rating,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        Page<BookSearchResultDTO> booksPage = bookService.searchBooks(query, genres, rating, pageable);
        return ResponseEntity.ok(booksPage);
    }
}
