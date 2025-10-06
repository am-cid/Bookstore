package com.simple.Bookstore.Theme;

import com.simple.Bookstore.User.User;
import lombok.NonNull;
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
    Optional<Theme> findByIdAndPublishedIsTrue(Long id);

    Optional<Theme> findByName(String name);

    @Query("""
            SELECT new com.simple.Bookstore.Theme.ThemeResponseDTO(
                t.id, t.name, t.description, t.date, p.id, u.username, p.displayName,
                t.base00, t.base01, t.base02, t.base03, t.base04, t.base05, t.base06, t.base07
            )
            FROM Theme t
            LEFT JOIN t.profile p
            LEFT JOIN p.user u
            WHERE t.id = :themeId
                AND ((t.published AND p.isPublic) OR (:profileId IS NOT NULL AND p.id = :profileId))
            """)
    Optional<ThemeResponseDTO> findPublishedOrOwnedThemeById(
            @Param("themeId") Long themeId,
            @Param("profileId") Long profileId
    );

    List<Theme> findByProfileUserOrderByName(User user);

    @Query("""
            SELECT new com.simple.Bookstore.Theme.ThemeResponseDTO(
                t.id, t.name, t.description, t.date, p.id, u.username, p.displayName,
                t.base00, t.base01, t.base02, t.base03, t.base04, t.base05, t.base06, t.base07
            )
            FROM Theme t
            LEFT JOIN t.profile p
            LEFT JOIN p.user u
            WHERE (t.published = true AND t.profile.isPublic = true)
                OR (:user IS NOT NULL AND t.profile.user = :user)
            ORDER BY t.date DESC, t.id DESC
            LIMIT :n
            """)
    List<ThemeResponseDTO> findLatestNPublishedOrOwnedThemes(
            @Param("user") User user,
            @Param("n") int n
    );

    @Query("""
            SELECT t.id
            FROM Theme t
            LEFT JOIN t.savedByProfiles p
            WHERE p.id = :profileId
            """)
    List<Long> findProfileSavedThemeIds(@Param("profileId") Long profileId);

    Page<Theme> findByPublishedIsTrue(Pageable pageable);

    @Query(value = """
            SELECT t from Theme t
            WHERE (t.published = true AND t.profile.isPublic = true)
                OR (:user IS NOT NULL AND t.profile.user = :user)
            """)
    Page<Theme> findByPublishedOrOwnedUnpublishedThemes(@Param("user") User user, Pageable pageable);

    @Query("""
            SELECT new com.simple.Bookstore.Theme.ThemeResponseDTO(
                t.id, t.name, t.description, t.date, p.id, u.username, p.displayName,
                t.base00, t.base01, t.base02, t.base03, t.base04, t.base05, t.base06, t.base07
            )
            FROM Theme t
            LEFT JOIN t.profile p
            LEFT JOIN p.user u
            WHERE p.id = :profileId
                AND ((t.published AND p.isPublic) OR (:visitorProfileId IS NOT NULL AND p.id = :visitorProfileId))
            """)
    Page<ThemeResponseDTO> findPublishedOrOwnedThemeByProfileId(
            @NonNull @Param("profileId") Long profileId,
            @Param("visitorProfileId") Long visitorProfileId,
            Pageable pageable
    );


    @Query("""
            SELECT new com.simple.Bookstore.Theme.ThemeResponseDTO(
                t.id, t.name, t.description, t.date, p.id, u.username, p.displayName,
                t.base00, t.base01, t.base02, t.base03, t.base04, t.base05, t.base06, t.base07
            )
            FROM Theme t
            LEFT JOIN t.savedByProfiles p
            LEFT JOIN p.user u
            WHERE p.id = :profileId
            """)
    Page<ThemeResponseDTO> findProfileSavedThemes(
            @Param("profileId") Long profileId,
            Pageable pageable
    );

    @Query(value = """
            SELECT t.id, t.name, t.description, t.date,
                   t.profile_id AS profileId,
                   u.username AS username,
                   p.display_name AS userDisplayName,
                   t.base00, t.base01, t.base02, t.base03, t.base04, t.base05, t.base06, t.base07
            FROM theme t
            LEFT JOIN profile p ON p.id = t.profile_id
            LEFT JOIN users u ON u.id = p.user_id
            WHERE (
                (:query IS NULL OR :query = '' OR :query <% t.name OR :query <% u.username OR :query <% p.display_name)
                AND (
                    (t.published = true AND p.is_public = true)
                    OR (:profileId IS NOT NULL AND t.profile_id = :profileId)
                )
            )
            ORDER BY
                CASE
                    WHEN :query IS NULL OR :query = '' THEN EXTRACT(EPOCH FROM t.date)
                    ELSE GREATEST(
                        word_similarity(:query, t.name),
                        word_similarity(:query, u.username),
                        word_similarity(:query, p.display_name),
                        0
                    )
                END, t.id
            DESC
            """, countQuery = """
            SELECT COUNT(t.id)
            FROM theme t
            LEFT JOIN profile p ON p.id = t.profile_id
            LEFT JOIN users u ON u.id = p.user_id
            WHERE (
                (:query IS NULL OR :query = '' OR :query <% t.name OR :query <% u.username OR :query <% p.display_name)
                AND (
                    (t.published = true AND p.is_public = true)
                    OR (:profileId IS NOT NULL AND t.profile_id = :profileId)
                )
            )
            """,
            nativeQuery = true)
    Page<ThemeProjection> searchThemes(@Param("query") String query,
                                       @Param("profileId") Long profileId,
                                       Pageable pageable);
}
