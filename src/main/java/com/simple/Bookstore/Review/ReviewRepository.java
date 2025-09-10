package com.simple.Bookstore.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllReviewsByBookId(Long bookId);


    @Query(value = """
            SELECT r.id, r.title, r.content, r.rating, r.date, r.edited,
                b.id as bookId , b.title as bookTitle, b.author as bookAuthor, b.front_image as bookFrontImage,
                u.username as username, p.display_name as userDisplayName,
                (
                    (
                        -- need minus one for 0-indexed paging
                        ROW_NUMBER() OVER (PARTITION BY r.book_id ORDER BY r.date DESC, r.id DESC) - 1
                    ) / :pageSize
                ) AS pageNumber
            FROM review r
            LEFT JOIN book b ON r.book_id = b.id
            LEFT JOIN profile p ON r.profile_id = p.id
            LEFT JOIN users u ON p.user_id = u.id
            ORDER BY r.date DESC, r.id DESC
            LIMIT :n
            """,
            nativeQuery = true)
    List<ReviewProfileViewProjection> findTopNByOrderByIdDesc(
            @Param("pageSize") Integer pageSize,
            int n
    );

    @Query(value = """
            SELECT r.id, r.title, r.content, r.rating, r.date, r.edited,
                b.id as bookId , b.title as bookTitle, b.author as bookAuthor, b.front_image as bookFrontImage,
                u.username as username, p.display_name as userDisplayName,
                (
                    (
                        -- need minus one for 0-indexed paging
                        ROW_NUMBER() OVER (PARTITION BY r.book_id ORDER BY r.date DESC, r.id DESC) - 1
                    ) / :pageSize
                ) AS pageNumber
            FROM review r
            LEFT JOIN book b ON r.book_id = b.id
            LEFT JOIN profile p ON r.profile_id = p.id
            LEFT JOIN users u ON p.user_id = u.id
            WHERE r.profile_id = :profileId
            ORDER BY r.date DESC, r.id DESC
            """, countQuery = """
            SELECT COUNT(r.id)
            FROM review r
            WHERE r.profile_id = :profileId
            """,
            nativeQuery = true
    )
    Page<ReviewProfileViewProjection> findAllReviewsByProfileId(
            @Param("profileId") Long profileId,
            @Param("pageSize") Integer pageSize,
            Pageable pageable
    );

    @Query(value = """
            SELECT r.id, r.title, r.content, r.rating, r.date, r.edited,
                b.id as bookId , b.title as bookTitle, b.author as bookAuthor, b.front_image as bookFrontImage,
                u.username as username, p.display_name as userDisplayName,
                (
                    (
                        -- need minus one for 0-indexed paging
                        ROW_NUMBER() OVER (PARTITION BY r.book_id ORDER BY r.date DESC, r.id DESC) - 1
                    ) / :pageSize
                ) AS pageNumber
            FROM review r
            LEFT JOIN book b ON r.book_id = b.id
            LEFT JOIN profile p ON r.profile_id = p.id
            LEFT JOIN users u ON p.user_id = u.id
            WHERE r.book_id = :bookId
            ORDER BY r.date DESC, r.id DESC
            """, countQuery = """
            SELECT COUNT(r.id)
            FROM review r
            WHERE r.book_id = :bookId
            """,
            nativeQuery = true
    )
    Page<ReviewProfileViewProjection> findAllReviewsByBookId(
            @Param("bookId") Long bookId,
            @Param("pageSize") Integer pageSize,
            Pageable pageable
    );

    @Query(value = """
            SELECT r.id, r.title, r.content, r.rating, r.date, r.edited,
                b.id as bookId , b.title as bookTitle, b.author as bookAuthor, b.front_image as bookFrontImage,
                u.username as username, p.display_name as userDisplayName,
                ARRAY_AGG(c_limited.id) as commentIds,
                ARRAY_AGG(c_limited.content) as commentContents,
                ARRAY_AGG(c_limited.date) as commentDates,
                ARRAY_AGG(c_limited.edited) as commentEdited,
                ARRAY_AGG(c_u.username) as commentUsernames,
                ARRAY_AGG(c_p.display_name) as commentUserDisplayNames
            FROM review r
            LEFT JOIN book b ON r.book_id = b.id
            LEFT JOIN profile p ON r.profile_id = p.id
            LEFT JOIN users u ON p.user_id = u.id
            -- subquery to limit queries comments since in the book view, reviews should only
            -- contain 2 comments with "see more" to look at all comments.
            LEFT JOIN LATERAL (
                SELECT c.id, c.content, c.date, c.edited, c.profile_id
                FROM comment c
                WHERE c.review_id = r.id
                ORDER BY c.date, c.id
                LIMIT 2
            ) as c_limited ON true
            LEFT JOIN profile c_p ON c_limited.profile_id = c_p.id
            LEFT JOIN users c_u ON c_p.user_id = c_u.id
            WHERE r.book_id = :bookId
            GROUP BY r.id, r.title, r.content, r.rating, r.date, r.edited,
                b.id, b.title, b.author, b.front_image,
                u.username, p.display_name
            ORDER BY r.date DESC, r.id DESC
            """, countQuery = """
            SELECT COUNT(r.id)
            FROM review r
            WHERE r.book_id = :bookId
            """,
            nativeQuery = true
    )
    Page<ReviewProjection> findAllReviewsByBookId(
            @Param("bookId") Long bookId,
            Pageable pageable
    );
}
