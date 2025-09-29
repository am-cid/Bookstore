package com.simple.Bookstore.Comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllCommentsByReviewId(Long reviewId);

    @Query(value = """
            SELECT c.id, c.content, c.date, c.edited,
                r.id as reviewId, r.title as reviewTitle,
                u_r.username as reviewerUsername, p_r.display_name reviewerDisplayName,
                b.id as bookId,
                u.username as username, p.display_name as userDisplayName,
                ( -- 1-indexed
                    SELECT FLOOR(COUNT(c2.id) / :pageSize) + 1
                    FROM comment c2
                    LEFT JOIN profile p2 ON c2.profile_id = p2.id
                    WHERE c2.review_id = c.review_id
                        AND c2.date < c.date -- less than since comments under review thread view is ORDER BY ASC so count older ones that come before it
                        AND (p2.is_public OR (:profileId IS NOT NULL AND p2.id = :profileId)) -- only query public or owned comments
                ) AS pageNumber
            FROM comment c
            LEFT JOIN review r ON c.review_id = r.id
            LEFT JOIN profile p_r ON r.profile_id = p_r.id
            LEFT JOIN users u_r ON p_r.user_id = u_r.id
            LEFT JOIN book b ON r.book_id = b.id
            LEFT JOIN profile p ON c.profile_id = p.id
            LEFT JOIN users u  ON p.user_id = u.id
            WHERE c.profile_id = :profileId
            ORDER BY c.date DESC, c.id DESC
            """, countQuery = """
            SELECT COUNT(c.id)
            FROM comment c
            WHERE c.profile_id = :profileId
            """,
            nativeQuery = true)
    Page<CommentProfileViewProjection> findAllCommentsByProfileId(
            @Param("profileId") Long profileId,
            @Param("pageSize") Integer pageSize,
            Pageable pageable
    );

    @Query("""
            SELECT new com.simple.Bookstore.Comment.CommentReviewViewResponseDTO(
                c.id, c.content, c.date, c.edited, u.username, p.displayName
            )
            FROM Comment c
            LEFT JOIN c.profile p
            LEFT JOIN p.user u
            WHERE c.review.id = :reviewId
                AND (p.isPublic = true OR (:profileId IS NOT NULL AND p.id = :profileId))
            """)
    Page<CommentReviewViewResponseDTO> findAllPublicOrOwnedByReviewId(
            @Param("reviewId") Long reviewId,
            @Param("profileId") Long profileId,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(c.id)
            FROM Comment c
            LEFT JOIN c.profile p
            WHERE c.review.id = :reviewId
                AND (p.isPublic = true OR (:profileId IS NOT NULL AND p.id = :profileId))
            """)
    Integer countAllPublicOrOwnedByReviewId(
            @Param("reviewId") Long reviewId,
            @Param("profileId") Long profileId
    );
}
