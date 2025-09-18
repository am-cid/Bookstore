package com.simple.Bookstore.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllPublicOrOwnedReviewsByBookId(Long bookId);

    @Query("""
            SELECT new com.simple.Bookstore.Review.ReviewViewResponseDTO(
                r.id, r.title, r.content, r.rating, r.date, r.edited,
                b.title, b.author, b.frontImage, u.username, p.displayName
            )
            FROM Review r
            LEFT JOIN r.book b
            LEFT JOIN r.profile p
            LEFT JOIN p.user u
            WHERE r.id = :id
            """)
    Optional<ReviewViewResponseDTO> findReviewById(@Param("id") Long id);

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
    Page<ReviewProfileViewProjection> findAllPublicOrOwnedReviewsByBookId(
            @Param("bookId") Long bookId,
            @Param("pageSize") Integer pageSize,
            Pageable pageable
    );

    @NativeQuery(value = """
            SELECT r.id, r.title, r.content, r.rating, r.date, r.edited,
                u.username as username, p.display_name as userDisplayName,
                (
                    SELECT COUNT(c.id)
                    FROM comment c
                    LEFT JOIN profile c_p ON c.profile_id = c_p.id
                    WHERE c.review_id = r.id
                    AND (
                        c_p.is_public = true
                        OR (:profileId IS NOT NULL AND c.profile_id = :profileId)
                    )
                ) AS commentCount,
                ARRAY_AGG(c_agg.id) as commentIds,
                ARRAY_AGG(c_agg.content) as commentContents,
                ARRAY_AGG(c_agg.date) as commentDates,
                ARRAY_AGG(c_agg.edited) as commentEdited,
                ARRAY_AGG(c_u.username) as commentUsernames,
                ARRAY_AGG(c_p.display_name) as commentUserDisplayNames
            FROM review r
            LEFT JOIN profile p ON r.profile_id = p.id
            LEFT JOIN users u ON p.user_id = u.id
            -- subquery to limit queries comments since in the book view, reviews should only
            -- contain 2 comments with "see more" to look at all comments.
            LEFT JOIN LATERAL (
                SELECT c.id, c.content, c.date, c.edited, c.profile_id
                FROM comment c
                LEFT JOIN profile c_p ON c.profile_id = c_p.id
                WHERE c.review_id = r.id
                    AND (
                        c_p.is_public = true
                        OR (:profileId IS NOT NULL AND c.profile_id = :profileId)
                    )
                ORDER BY c.date, c.id
                LIMIT 2
            ) as c_agg ON true
            LEFT JOIN profile c_p ON c_agg.profile_id = c_p.id
            LEFT JOIN users c_u ON c_p.user_id = c_u.id
            WHERE r.book_id = :bookId
                AND (
                    p.is_public = true
                    OR (:profileId IS NOT NULL AND r.profile_id = :profileId)
                )
            GROUP BY r.id, r.title, r.content, r.rating, r.date, r.edited,
                u.username, p.display_name
            ORDER BY r.date DESC, r.id DESC
            """, countQuery = """
            SELECT COUNT(r.id)
            FROM review r
            LEFT JOIN profile p ON r.profile_id = p.id
            WHERE r.book_id = :bookId
                AND (
                    p.is_public = true
                    OR (:profileId IS NOT NULL AND r.profile_id = :profileId)
                )
            """)
    Page<ReviewBookViewProjection> findAllPublicOrOwnedReviewsByBookId(
            @Param("bookId") Long bookId,
            @Param("profileId") Long profileId,
            Pageable pageable
    );

    List<Review> findByBookIdAndProfileId(Long bookId, Long profileId);

    Long countByBookId(Long bookId);
}
