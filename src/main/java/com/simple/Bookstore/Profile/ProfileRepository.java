package com.simple.Bookstore.Profile;

import com.simple.Bookstore.User.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);

    Optional<Profile> findByUserUsername(String username);

    Optional<Profile> findByIdAndIsPublicIsTrue(Long id);

    Optional<Profile> findByUserUsernameAndIsPublicIsTrue(String username);

    @Query(value = """
            SELECT p from Profile p
            WHERE p.isPublic = true
                or (p.isPublic = false AND p.user = :user)
            """)
    Page<Profile> findPublicOrOwnPrivateProfile(@Param("user") User user, Pageable pageable);

    @Query(value = """
            SELECT p.id, u.username, p.display_name, p.is_public
            FROM profile p
            LEFT JOIN users u ON u.id = p.user_id
            WHERE (:query IS NULL OR :query = '' OR :query <% u.username OR :query <% p.display_name)
                AND (p.is_public = true OR (p.user_id IS NOT NULL AND p.user_id = :userId))
            ORDER BY
                GREATEST(
                    word_similarity(:query, u.username),
                    word_similarity(:query, p.display_name),
                    0
                ) DESC,
                u.username
            """, countQuery = """
            SELECT COUNT(DISTINCT p.id)
            FROM profile p
            LEFT JOIN users u ON u.id = p.user_id
            WHERE (:query IS NULL OR :query = '' OR :query <% u.username OR :query <% p.display_name)
                AND (p.is_public = true OR (p.user_id IS NOT NULL AND p.user_id = :userId))
            """,
            nativeQuery = true)
    Page<ProfileProjection> searchProfiles(
            @Param("query") String query,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
