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
                u.username as username, p.display_name as userDisplayName
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
    Page<ReviewProjection> findAllReviewsByBookId(
            @Param("bookId") Long bookId,
            Pageable pageable
    );
}
