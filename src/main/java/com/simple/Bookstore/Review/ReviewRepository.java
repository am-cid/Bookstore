package com.simple.Bookstore.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
