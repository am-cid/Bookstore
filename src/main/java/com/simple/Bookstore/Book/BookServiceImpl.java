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
    public List<BookSearchResultDTO> findTopNRatedBooks(int n) {
        return bookRepo
                .findTopNRatedBooks(n)
                .stream()
                .map(this::searchResultProjectionToDTO)
                .toList();
    }

    @Override
    public List<BookSearchResultDTO> findLatestNBooks(int n) {
        return bookRepo
                .findLatestNBooks(n)
                .stream()
                .map(this::searchResultProjectionToDTO)
                .toList();
    }

    @Override
    public List<String> findDistinctAuthors() {
        return bookRepo.findDistinctAuthors();
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
            // return none if no parameters
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        } else if (query == null && rating == null) {
            // return all then filter by genre if genre is the only parameter
            List<BookSearchResultDTO> filteredDTOs = bookRepo
                    .findAll()
                    .stream()
                    .map(this::bookToSearchResultDTO)
                    .filter(dto -> dto
                            .genres()
                            .stream()
                            .anyMatch(genres::contains)
                    )
                    .toList();
            return new PageImpl<>(filteredDTOs, pageable, filteredDTOs.size());
        } else {
            // filter normally
            List<BookSearchResultDTO> filteredDTOs = bookRepo
                    .searchBooks(query, rating, pageable)
                    .stream()
                    .map(this::searchResultProjectionToDTO)
                    .filter(dto -> genres == null || dto.genres().stream()
                            .anyMatch(genres::contains))
                    .toList();
            return new PageImpl<>(filteredDTOs, pageable, filteredDTOs.size());
        }
    }

    // HELPERS

    private Double calculateRelevancy(
            BookRelevanceProjection projection,
            double maxRating,
            long maxReviewCount,
            long maxDateProxy
    ) {
        double normalizedRating = (projection.getAverageRating() != null) ? projection.getAverageRating() / maxRating : 0.0;
        double normalizedReviewCount = (projection.getReviewCount() != null) ? (double) projection.getReviewCount() / maxReviewCount : 0.0;

        double normalizedDate = (double) projection.getId() / maxDateProxy;
        return (normalizedRating * RATING_WEIGHT) +
                (normalizedReviewCount * REVIEW_COUNT_WEIGHT) +
                (normalizedDate * DATE_WEIGHT);
    }

    private Book createDtoToBook(BookRequestDTO dto) {
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

    private void updateOldBookWithRequestDTO(Book oldBook, BookRequestDTO newBook) {
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
        Book book = bookRepo
                .findById(projection.getId())
                .orElseThrow(() -> new BookNotFoundException(projection.getId()));
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
