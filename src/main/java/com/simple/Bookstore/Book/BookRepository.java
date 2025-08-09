package com.simple.Bookstore.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.front_image, b.back_image, b.spine_image
            FROM book b
            ORDER BY b.id DESC
            LIMIT :n
            """, nativeQuery = true)
    List<BookSearchResultProjection> findLatestNBooks(int n);

    @Query("SELECT DISTINCT b.author FROM Book b ORDER BY b.author")
    List<String> findDistinctAuthors();


    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.front_image, b.back_image, b.spine_image,
                AVG(r.rating), COUNT(r.id)
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            GROUP BY b.id
            """,
            nativeQuery = true)
    List<BookRelevanceProjection> findBooksForScoring();

    @Query(value = """
            SELECT b.id, b.title, b.author, b.description, b.front_image, b.back_image, b.spine_image,
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
            SELECT b.id, b.title, b.author, b.description, b.front_image, b.back_image, b.spine_image,
                   AVG(r.rating) AS averageRating
            FROM book b
            LEFT JOIN review r ON b.id = r.book_id
            WHERE (:query IS NULL OR :query <% b.title OR :query <% b.author)
            GROUP BY b.id
            HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
            ORDER BY GREATEST(word_similarity(:query, b.title), word_similarity(:query, b.author)) DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT b.id)
                    FROM book b
                    LEFT JOIN review r ON b.id = r.book_id
                    WHERE (:query IS NULL OR :query <% b.title OR :query <% b.author)
                    GROUP BY b.id
                    HAVING (:rating IS NULL OR AVG(r.rating) >= :rating)
                    """,
            nativeQuery = true)
    Page<BookSearchResultProjection> searchBooks(@Param("query") String query,
                                                 @Param("rating") Double rating,
                                                 Pageable pageable);

}
