package com.simple.Bookstore.Book;

import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Review.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;

    @Override
    public List<BookSearchResultDTO> findAllBooks() {
        return bookRepo.findAll().stream()
                .map(this::bookToSearchResultDTO)
                .toList();
    }

    @Override
    public Optional<BookSearchResultDTO> findBookById(Long id) {
        return bookRepo.findById(id).map(this::bookToSearchResultDTO);
    }

    @Override
    @Transactional
    public Book createBook(BookRequestDTO bookDTO) {
        return bookRepo.save(createDtoToBook(bookDTO));
    }

    @Override
    @Transactional
    public Book updateBook(Long id, BookRequestDTO book) {
        return bookRepo.findById(id)
                .map(existingBook -> {
                    updateOldBookWithRequestDTO(existingBook, book);
                    return bookRepo.save(existingBook);
                })
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // ... other methods ...

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepo.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepo.deleteById(id);
    }

    @Override
    public Page<BookSearchResultDTO> searchBooks(String query, Set<Genre> genres, Double rating, Pageable pageable) {
        if (query == null && genres == null && rating == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        Page<BookSearchResultProjection> bookProjections = bookRepo.searchBooks(query, rating, pageable);
        List<BookSearchResultDTO> filteredDTOs = bookProjections.stream()
                .map(this::searchResultProjectionToDTO)
                .filter(dto -> genres == null || dto.genres().stream()
                        .anyMatch(g -> genres.contains(g.name())))
                .toList();
        return new PageImpl<>(filteredDTOs, pageable, filteredDTOs.size());
    }

    public Book createDtoToBook(BookRequestDTO dto) {
        Book book = new Book();
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setDescription(dto.description());
        book.setGenres(dto.genres());
        book.setFrontImage(dto.frontImage());
        book.setBackImage(dto.backImage());
        book.setSpineImage(dto.spineImage());
        book.setContentImages(dto.contentImages());
        return book;
    }

    public void updateOldBookWithRequestDTO(Book oldBook, BookRequestDTO newBook) {
        oldBook.setTitle(newBook.title() != null ? newBook.title() : oldBook.getTitle());
        oldBook.setAuthor(newBook.author() != null ? newBook.author() : oldBook.getAuthor());
        oldBook.setDescription(newBook.description() != null ? newBook.description() : oldBook.getDescription());
        oldBook.extendGenres(newBook.genres());
        oldBook.setFrontImage(newBook.frontImage() != null ? newBook.frontImage() : oldBook.getFrontImage());
        oldBook.setBackImage(newBook.backImage() != null ? newBook.backImage() : oldBook.getBackImage());
        oldBook.setSpineImage(newBook.spineImage() != null ? newBook.spineImage() : oldBook.getSpineImage());
        oldBook.extendContentImages(newBook.contentImages());
    }

    /**
     * Helper method to perform the mapping from projection to DTO
     */
    private BookSearchResultDTO searchResultProjectionToDTO(BookSearchResultProjection projection) {
        // Fetch the full entity to get the genres collection, which is not available in the projection.
        // This is a new, separate database call for each book on the page.
        Book book = bookRepo.findById(projection.getId()).orElse(null);
        if (book == null) {
            return null;
        }
        return new BookSearchResultDTO(
                projection.getId(),
                projection.getTitle(),
                projection.getAuthor(),
                projection.getDescription(),
                book.getGenres(),
                projection.getAverageRating(),
                projection.getFrontImage(),
                projection.getBackImage(),
                projection.getSpineImage(),
                projection.getContentImages()
        );
    }

    /**
     * Converts a Book entity to a BookSearchResultDTO.
     *
     * @param book The Book entity to convert.
     * @return The corresponding BookSearchResultDTO.
     */
    private BookSearchResultDTO bookToSearchResultDTO(Book book) {
        OptionalDouble averageRating = book.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average();
        Double avgRating = averageRating.isPresent() ? averageRating.getAsDouble() : null;
        return new BookSearchResultDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getGenres(),
                avgRating,
                book.getFrontImage(),
                book.getBackImage(),
                book.getSpineImage(),
                book.getContentImages()
        );
    }
}
