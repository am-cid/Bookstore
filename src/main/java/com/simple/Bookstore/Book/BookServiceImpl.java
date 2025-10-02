package com.simple.Bookstore.Book;

import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.User.User;
import com.simple.Bookstore.User.UserRepository;
import com.simple.Bookstore.utils.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final double RATING_WEIGHT = 0.70;
    private static final double REVIEW_COUNT_WEIGHT = 0.20;
    private static final double DATE_WEIGHT = 0.10;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    // HELPERS

    private static Double calculateRelevancy(
            BookRelevanceProjection projection,
            double maxRating,
            long maxReviewCount,
            LocalDateTime maxDate
    ) {
        double normalizedRating = (projection.getAverageRating() != null)
                ? projection.getAverageRating() / maxRating
                : 0.0;
        double normalizedReviewCount = (projection.getReviewCount() != null)
                ? (double) projection.getReviewCount() / maxReviewCount
                : 0.0;
        long daysSincePublished = ChronoUnit.DAYS.between(projection.getDate(), maxDate);
        double normalizedDate = Math.max(0.0, 1.0 - (daysSincePublished / 365.0));

        return (normalizedRating * RATING_WEIGHT) +
                (normalizedReviewCount * REVIEW_COUNT_WEIGHT) +
                (normalizedDate * DATE_WEIGHT);
    }

    private static void updateOldBookWithRequestDTO(Book oldBook, BookRequestDTO newBook) {
        oldBook.setTitle(newBook.title() != null ? newBook.title() : oldBook.getTitle());
        oldBook.setAuthor(newBook.author() != null ? newBook.author() : oldBook.getAuthor());
        oldBook.setDescription(newBook.description() != null ? newBook.description() : oldBook.getDescription());
        oldBook.extendGenres(newBook.genres());
        oldBook.setFrontImage(newBook.frontImage() != null ? newBook.frontImage() : oldBook.getFrontImage());
        oldBook.setBackImage(newBook.backImage() != null ? newBook.backImage() : oldBook.getBackImage());
        oldBook.setSpineImage(newBook.spineImage() != null ? newBook.spineImage() : oldBook.getSpineImage());
        oldBook.extendContentImages(newBook.contentImages());
    }

    @Override
    public List<BookSearchResultDTO> findAllBooks() {
        return bookRepo.findAll().stream()
                .map(BookMapper::bookToSearchResultDTO)
                .toList();
    }

    @Override
    public List<BookSearchResultDTO> findTopNRatedBooks(int n) {
        return bookRepo
                .findTopNRatedBooks(n)
                .stream()
                .map(BookMapper::searchResultProjectionToDTO)
                .toList();
    }

    @Override
    public List<BookPreviewDTO> findLatestNBooks(int n) {
        return bookRepo
                .findLatestNBooks(n)
                .stream()
                .map(BookMapper::previewProjectionToDTO)
                .toList();
    }

    @Override
    public List<String> findDistinctAuthors() {
        return bookRepo.findDistinctAuthors();
    }

    @Override
    public List<BookSearchResultDTO> findRelevantBooks(int limit) {
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
        LocalDateTime maxDateProxy = books
                .stream()
                .map(BookRelevanceProjection::getDate)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);
        return books
                .stream()
                .sorted((p1, p2) -> {
                    Double score1 = calculateRelevancy(p1, maxRating, maxReviewCount, maxDateProxy);
                    Double score2 = calculateRelevancy(p2, maxRating, maxReviewCount, maxDateProxy);
                    return Double.compare(score2, score1);
                })
                .map(projection -> BookMapper.relevancyProjectionToDTO(projection, bookRepo))
                .toList()
                .subList(0, limit);
    }

    @Override
    @Transactional
    public List<BookSearchResultDTO> findSavedBooks(User user) {
        if (user == null)
            return List.of();
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepo.findById(user.getId()).get();
        return managedUser
                .getProfile()
                .getSavedBooks()
                .stream()
                .map(BookMapper::bookToSearchResultDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<Long> findSavedBookIds(User user) {
        if (user == null)
            return List.of();
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepo.findById(user.getId()).get();
        return bookRepo
                .findProfileSavedBookIds(managedUser.getProfile().getId());
    }

    @Override
    @Transactional
    public Page<BookSearchResultDTO> findSavedBooks(User user, Pageable pageable) {
        if (user == null)
            return Page.empty(pageable);
        // need to re-get user since the User passed in is from @AuthenticationPrincipal,
        // which is outside the @Transactional block of this method.
        // This was a headache and a half!
        User managedUser = userRepo.findById(user.getId()).get();
        return bookRepo
                .findProfileSavedBooks(
                        managedUser.getProfile().getId(),
                        pageable
                )
                .map(BookMapper::searchResultProjectionToDTO);

    }

    @Override
    public Optional<BookSearchResultDTO> findBookById(Long id) {
        return bookRepo
                .findBookById(id)
                .map(BookMapper::searchResultProjectionToDTO);
    }

    @Override
    @Transactional
    public Book createBook(BookRequestDTO bookDTO) {
        return bookRepo.save(BookMapper.createDtoToBook(bookDTO));
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
    public Page<BookSearchResultDTO> searchBooks(
            Optional<String> query,
            Optional<Set<Genre>> genres,
            Optional<Double> rating,
            Pageable pageable
    ) {
        Set<String> validGenres = genres.orElse(new HashSet<>())
                .stream()
                .filter(Objects::nonNull)
                .map(Genre::name)
                .collect(Collectors.toSet());
        Page<BookSearchResultProjection> bookPage = bookRepo.searchBooks(
                query.orElse(null),
                rating.orElse(null),
                validGenres,
                validGenres.size(),
                pageable
        );
        return bookPage
                .map(BookMapper::searchResultProjectionToDTO);
    }
}
