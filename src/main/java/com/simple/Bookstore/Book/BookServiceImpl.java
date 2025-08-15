package com.simple.Bookstore.Book;

import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.utils.BookDtoConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final double RATING_WEIGHT = 0.70;
    private static final double REVIEW_COUNT_WEIGHT = 0.20;
    private static final double DATE_WEIGHT = 0.10;
    private final BookRepository bookRepo;

    @Override
    public List<BookSearchResultDTO> findAllBooks() {
        return bookRepo.findAll().stream()
                .map(BookDtoConverter::bookToSearchResultDTO)
                .toList();
    }

    @Override
    public List<BookSearchResultDTO> findTopNRatedBooks(int n) {
        return bookRepo
                .findTopNRatedBooks(n)
                .stream()
                .map(projection -> BookDtoConverter.searchResultProjectionToDTO(projection, bookRepo))
                .toList();
    }

    @Override
    public List<BookSearchResultDTO> findLatestNBooks(int n) {
        return bookRepo
                .findLatestNBooks(n)
                .stream()
                .map(projection -> BookDtoConverter.searchResultProjectionToDTO(projection, bookRepo))
                .toList();
    }

    @Override
    public List<String> findDistinctAuthors() {
        return bookRepo.findDistinctAuthors();
    }

    @Override
    public List<BookSearchResultDTO> findRelevantBooks() {
        List<BookRelevanceProjection> books = bookRepo.findBooksForScoring();
        // normalize
        double maxRating = books
                .stream()
                .mapToDouble(p -> p.getAverageRating() != null ? p.getAverageRating() : 0.0)
                .max()
                .orElse(1.0);
        long maxReviewCount = books
                .stream()
                .mapToLong(p -> p.getReviewCount() != null ? p.getReviewCount() : 0L)
                .max()
                .orElse(1L);
        long maxDateProxy = books
                .stream()
                .mapToLong(BookRelevanceProjection::getId)
                .max()
                .orElse(1L);
        return books
                .stream()
                .sorted((p1, p2) -> {
                    Double score1 = calculateRelevancy(p1, maxRating, maxReviewCount, maxDateProxy);
                    Double score2 = calculateRelevancy(p2, maxRating, maxReviewCount, maxDateProxy);
                    return Double.compare(score2, score1);
                })
                .map(projection -> BookDtoConverter.relevancyProjectionToDTO(projection, bookRepo))
                .toList();
    }

    @Override
    public Optional<BookSearchResultDTO> findBookById(Long id) {
        return bookRepo.findById(id).map(BookDtoConverter::bookToSearchResultDTO);
    }

    @Override
    @Transactional
    public Book createBook(BookRequestDTO bookDTO) {
        return bookRepo.save(BookDtoConverter.createDtoToBook(bookDTO));
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
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        book.getSavedByProfiles().forEach(profile -> profile.getSavedBooks().remove(book));
        bookRepo.delete(book);
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
                    .map(BookDtoConverter::bookToSearchResultDTO)
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
                    .map(projection -> BookDtoConverter.searchResultProjectionToDTO(projection, bookRepo))
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
}
