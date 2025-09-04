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
            SELECT r.*
            FROM review r
            ORDER BY r.id DESC
            LIMIT :n
            """, nativeQuery = true)
    List<Review> findTopNByOrderByIdDesc(int n);

    @Query("""
            SELECT new com.simple.Bookstore.Review.ReviewResponseDTO(
                        r.id, r.title, r.content, r.rating, r.date, r.edited,
                        b.id, b.title, b.author, b.frontImage,
                        u.username, p.displayName
            )
            FROM Review r
            LEFT JOIN r.book b
            LEFT JOIN r.profile p
            LEFT JOIN p.user u
            WHERE r.profile.id = :profileId
            ORDER BY r.date DESC, r.id DESC
            """)
    Page<ReviewResponseDTO> findAllReviewsByProfileId(@Param("profileId") Long profileId, Pageable pageable);
}
