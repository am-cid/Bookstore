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

    @Query("""
            SELECT p.id, p.user.username, p.displayName from Profile p
            WHERE p.isPublic = true
                or (p.isPublic = false AND p.user = :user)
            """)
    Page<ProfileProjection> searchProfiles(
            @Param("query") String query,
            @Param("user") User user,
            Pageable pageable
    );
}
