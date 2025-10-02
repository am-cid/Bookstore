package com.simple.Bookstore.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @NativeQuery("""
            SELECT
                b.id, b.title, b.author, b.description, b.date, b.front_image, b.back_image, b.spine_image,
                AVG(r.rating) AS averageRating,
                ARRAY_AGG(DISTINCT bg.genre) AS genres,
                ARRAY_AGG(DISTINCT image_url) AS contentImages
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            LEFT JOIN book_content_images bci ON b.id = bci.book_id
            WHERE b.id = :id
            GROUP BY b.id, b.title, b.author, b.description, b.date, b.front_image, b.back_image, b.spine_image
            """)
    Optional<BookSearchResultProjection> findBookById(@Param("id") Long id);

    @Query(value = """
            SELECT b.id, b.title, b.author, b.front_image,
                AVG(r.rating) as averageRating,
                ARRAY_AGG(bg.genre) AS genres
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            GROUP BY b.id
            ORDER BY b.id DESC
            LIMIT :n
            """, nativeQuery = true)
    List<BookPreviewProjection> findLatestNBooks(int n);

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

    @Query("""
            SELECT b.id
            FROM Book b
            LEFT JOIN b.savedByProfiles p
            WHERE p.id = :profileId
            """)
    List<Long> findProfileSavedBookIds(@Param("profileId") Long profileId);

    @NativeQuery("""
            SELECT b.id, b.title, b.author, b.description, b.date,
                b.front_image, b.back_image, b.spine_image,
                AVG(r.rating) AS averageRating,
                ARRAY_AGG(bg.genre) AS genres,
                ARRAY_AGG(bci.image_url) AS contentImages
            FROM book b
            LEFT JOIN profile_saved_books psb ON b.id = psb.book_id
            LEFT JOIN profile p ON psb.profile_id = p.id
            LEFT JOIN review r ON b.id = r.book_id
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            LEFT JOIN book_content_images bci ON b.id = bci.book_id
            WHERE p.id = :profileId
            GROUP BY b.id, b.title, b.author, b.description, b.date,
                b.front_image, b.back_image, b.spine_image
            """)
    Page<BookSearchResultProjection> findProfileSavedBooks(
            @Param("profileId") Long profileId,
            Pageable pageable
    );

    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.date,
                b.front_image, b.back_image, b.spine_image,
                AVG(r.rating) AS averageRating,
                ARRAY_AGG(bg.genre) AS genres,
                ARRAY_AGG(bci.image_url) AS contentImages
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            LEFT JOIN book_genres bg ON b.id = bg.book_id
            LEFT JOIN book_content_images bci ON b.id = bci.book_id
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
            SELECT COUNT(b.id)
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
            nativeQuery = true
    )
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
            SELECT COUNT(b.id)
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            GROUP BY b.id
            """, nativeQuery = true
    )
    Page<BookSearchResultProjection> findAllAsPage(Pageable pageable);
}
