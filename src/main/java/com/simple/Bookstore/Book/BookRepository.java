package com.simple.Bookstore.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.date, b.front_image, b.back_image, b.spine_image,
                STRING_AGG(bg.genre, ',') AS genres
            FROM book b
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            GROUP BY b.id
            ORDER BY b.id DESC
            LIMIT :n
            """, nativeQuery = true)
    List<BookSearchResultProjection> findLatestNBooks(int n);

    @Query("SELECT DISTINCT b.author FROM Book b ORDER BY b.author")
    List<String> findDistinctAuthors();


    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.date, b.front_image, b.back_image, b.spine_image,
                AVG(r.rating) as averageRating, COUNT(r.id) as reviewCount,
                STRING_AGG(bg.genre, ',') AS genres
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            GROUP BY b.id
            """,
            nativeQuery = true)
    List<BookRelevanceProjection> findBooksForScoring();

    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.date, b.front_image, b.back_image, b.spine_image,
                   AVG(r.rating) AS averageRating
            FROM book b
            JOIN review r ON b.id = r.book_id
            GROUP BY b.id
            ORDER BY AVG(r.rating) DESC
            LIMIT :n
            """,
            nativeQuery = true)
    List<BookSearchResultProjection> findTopNRatedBooks(@Param("n") int n);

    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.date,
            b.front_image, b.back_image, b.spine_image,
                        AVG(r.rating) AS averageRating,
                        (SELECT STRING_AGG(bg_all.genre, ',') AS genres
                         FROM book_genres bg_all
                         WHERE bg_all.book_id = b.id) AS genres
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            WHERE b.id IN (
                SELECT bg_filter.book_id
                FROM book_genres bg_filter
                WHERE (:genres IS NULL OR bg_filter.genre IN (:genres))
                GROUP BY bg_filter.book_id
                HAVING (:genreCount = 0 OR COUNT(bg_filter.genre) = :genreCount)
            )
                AND (:query IS NULL OR :query = '' OR :query <% b.title OR :query <% b.author)
            GROUP BY b.id
            HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
            ORDER BY
                CASE
                    WHEN :query IS NULL OR :query = '' THEN EXTRACT(EPOCH FROM b.date)
                    ELSE GREATEST(word_similarity(:query, b.title), word_similarity(:query, b.author), 0)
                END, b.id
            DESC
            """, countQuery = """
            SELECT COUNT(DISTINCT b.id)
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            WHERE b.id IN (
                SELECT bg_filter.book_id
                FROM book_genres bg_filter
                WHERE (:genres IS NULL OR bg_filter.genre IN (:genres))
                GROUP BY bg_filter.book_id
                HAVING (:genreCount = 0 OR COUNT(bg_filter.genre) >= :genreCount)
            )
            AND (:query IS NULL OR :query = '' OR :query <% b.title OR :query <% b.author)
            GROUP BY b.id
            HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
            """,
            nativeQuery = true)
    Page<BookSearchResultProjection> searchBooks(
            @Param("query") String query,
            @Param("rating") Double rating,
            @Param("genres") Set<String> genres,
            @Param("genreCount") int genreCount,
            Pageable pageable
    );

    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.date, b.front_image, b.back_image, b.spine_image,
                   AVG(r.rating) AS averageRating
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            GROUP BY b.id
            """, countQuery = """
            SELECT COUNT(DISTINCT b.id)
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            GROUP BY b.id
            """, nativeQuery = true)
    Page<BookSearchResultProjection> findAllAsPage(Pageable pageable);
}
