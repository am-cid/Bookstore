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

    @Query("""
            SELECT new com.simple.Bookstore.Comment.CommentResponseDTO(
                        c.id, c.content, c.date, c.edited,
                        r.id, r.title, u_r.username, p_r.displayName,
                        b.id,
                        u.username, p.displayName
            )
            FROM Comment c
            LEFT JOIN c.review r
            LEFT JOIN r.profile p_r
            LEFT JOIN p_r.user u_r
            LEFT JOIN r.book b
            LEFT JOIN c.profile p
            LEFT JOIN p.user u
            WHERE c.profile.id = :profileId
            ORDER BY c.date DESC, c.id DESC
            """)
    Page<CommentResponseDTO> findAllCommentsByProfileId(@Param("profileId") Long profileId, Pageable pageable);
}
