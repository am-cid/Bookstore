package com.simple.Bookstore.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
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
                    SELECT count(DISTINCT b.id)
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
