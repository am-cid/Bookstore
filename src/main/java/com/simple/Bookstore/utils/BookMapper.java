package com.simple.Bookstore.utils;

import com.simple.Bookstore.Book.*;
import com.simple.Bookstore.Exceptions.BookNotFoundException;
import com.simple.Bookstore.Genre.Genre;
import com.simple.Bookstore.Review.Review;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class BookMapper {
    public static Book createDtoToBook(BookRequestDTO dto) {
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

    /**
     * Helper method to perform the mapping from projection to DTO
     */
    public static BookSearchResultDTO searchResultProjectionToDTO(
            BookSearchResultProjection projection
    ) {
        return new BookSearchResultDTO(
                projection.getId(),
                projection.getTitle(),
                projection.getAuthor(),
                projection.getDescription(),
                projection.getDate(),
                projection.getGenres() == null
                        ? null
                        : Arrays.stream(projection.getGenres()).map(Genre::valueOf).collect(Collectors.toSet()),
                projection.getAverageRating(),
                projection.getFrontImage(),
                projection.getBackImage(),
                projection.getSpineImage(),
                projection.getContentImages() == null
                        ? null
                        : Arrays.stream(projection.getContentImages()).collect(Collectors.toSet())
        );
    }

    /**
     * Converts a Book entity to a BookSearchResultDTO.
     *
     * @param book The Book entity to convert.
     * @return The corresponding BookSearchResultDTO.
     */
    public static BookSearchResultDTO bookToSearchResultDTO(Book book) {
        OptionalDouble averageRating = book
                .getReviews()
                .stream()
                .mapToDouble(Review::getRating)
                .average();
        Double avgRating = averageRating.isPresent() ? averageRating.getAsDouble() : null;
        return new BookSearchResultDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getDescription(),
                book.getDate(),
                book.getGenres(),
                avgRating,
                book.getFrontImage(),
                book.getBackImage(),
                book.getSpineImage(),
                book.getContentImages()
        );
    }

    public static BookSearchResultDTO relevancyProjectionToDTO(
            BookRelevanceProjection projection,
            BookRepository bookRepo
    ) {
        Book book = bookRepo
                .findById(projection.getId())
                .orElseThrow(() -> new BookNotFoundException(projection.getId()));
        return new BookSearchResultDTO(
                projection.getId(),
                projection.getTitle(),
                projection.getAuthor(),
                projection.getDescription(),
                projection.getDate(),
                book.getGenres(),
                projection.getAverageRating(),
                projection.getFrontImage(),
                projection.getBackImage(),
                projection.getSpineImage(),
                book.getContentImages()
        );
    }
}
