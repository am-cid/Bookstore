package com.simple.Bookstore.Theme;

import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    List<Theme> findByProfileUser(User user);

    Page<Theme> findByPublishedIsTrue(Pageable pageable);

    Optional<Theme> findByIdAndPublishedIsTrue(Long id);

    Optional<Theme> findByName(String name);

    @Query(value = """
            SELECT t from Theme t
            WHERE t.published = true
                or (t.published = false AND t.profile.user = :user)
            """)
    Page<Theme> findByPublishedOrOwnedUnpublishedThemes(@Param("user") User user, Pageable pageable);

    @Query(value = """
            SELECT t.id, t.name, t.description,
                   t.profile_id AS profileId,
                   u.username AS username,
                   p.display_name AS userDisplayName,
                   t.base00, t.base01, t.base02, t.base03, t.base04, t.base05, t.base06, t.base07
            FROM theme t
            LEFT JOIN profile p ON p.id = t.profile_id
            LEFT JOIN users u ON u.id = p.user_id
            WHERE (
                (:query IS NULL OR :query <% t.name)
                AND (
                    t.published = true
                    OR (:profileId IS NOT NULL AND t.profile_id = :profileId)
                )
            )
            ORDER BY GREATEST(word_similarity(:query, t.name)) DESC
            """,
            countQuery = """
                        SELECT COUNT(*)
                        FROM theme t
                        WHERE (
                            (:query IS NULL OR :query <% t.name)
                            AND (
                                t.published = true
                                OR (:profileId IS NOT NULL AND t.profile_id = :profileId)
                            )
                        )
                    """,
            nativeQuery = true)
    Page<ThemeProjection> searchThemes(@Param("query") String query,
                                       @Param("profileId") Long profileId,
                                       Pageable pageable);
}
