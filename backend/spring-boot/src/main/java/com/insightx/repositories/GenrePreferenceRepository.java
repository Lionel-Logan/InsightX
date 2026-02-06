package com.insightx.repositories;

import com.insightx.entities.GenrePreference;
import com.insightx.entities.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * GenrePreferenceRepository - Data access for user genre preferences
 * 
 * Manages both explicit (user-selected) and implicit (calculated) genre preferences
 * with 1-10 scoring system for personalized recommendations.
 */
@Repository
public interface GenrePreferenceRepository extends JpaRepository<GenrePreference, UUID> {

    /**
     * Find all genre preferences for a user
     */
    List<GenrePreference> findByUserId(UUID userId);

    /**
     * Find genre preferences by media type
     */
    List<GenrePreference> findByUserIdAndMediaType(UUID userId, MediaType mediaType);

    /**
     * Find specific genre preference
     */
    Optional<GenrePreference> findByUserIdAndGenreAndMediaType(UUID userId, String genre, MediaType mediaType);

    /**
     * Check if genre preference exists
     */
    boolean existsByUserIdAndGenreAndMediaType(UUID userId, String genre, MediaType mediaType);

    /**
     * Find all explicit (user-selected) preferences
     */
    List<GenrePreference> findByUserIdAndExplicitTrue(UUID userId);

    /**
     * Find all implicit (calculated) preferences
     */
    List<GenrePreference> findByUserIdAndExplicitFalse(UUID userId);

    /**
     * Find high-scoring preferences (score >= threshold)
     */
    @Query("SELECT gp FROM GenrePreference gp WHERE gp.userId = :userId AND gp.preferenceScore >= :minScore ORDER BY gp.preferenceScore DESC")
    List<GenrePreference> findByUserIdAndMinScore(@Param("userId") UUID userId, @Param("minScore") int minScore);

    /**
     * Get top N genres by score for a media type
     */
    @Query("SELECT gp FROM GenrePreference gp WHERE gp.userId = :userId AND gp.mediaType = :mediaType ORDER BY gp.preferenceScore DESC LIMIT :limit")
    List<GenrePreference> findTopGenresByMediaType(@Param("userId") UUID userId, @Param("mediaType") MediaType mediaType, @Param("limit") int limit);

    /**
     * Count preferences by user
     */
    long countByUserId(UUID userId);

    /**
     * Count explicit preferences by user and media type
     */
    long countByUserIdAndMediaTypeAndExplicitTrue(UUID userId, MediaType mediaType);

    /**
     * Delete all genre preferences for a user
     */
    @Modifying
    @Query("DELETE FROM GenrePreference gp WHERE gp.userId = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    /**
     * Delete all explicit preferences for a user (reset onboarding)
     */
    @Modifying
    @Query("DELETE FROM GenrePreference gp WHERE gp.userId = :userId AND gp.explicit = true")
    void deleteExplicitPreferences(@Param("userId") UUID userId);

    /**
     * Delete all implicit preferences for a user (force recalculation)
     */
    @Modifying
    @Query("DELETE FROM GenrePreference gp WHERE gp.userId = :userId AND gp.explicit = false")
    void deleteImplicitPreferences(@Param("userId") UUID userId);

    /**
     * Get all distinct genres for a media type across all users (for analytics)
     */
    @Query("SELECT DISTINCT gp.genre FROM GenrePreference gp WHERE gp.mediaType = :mediaType")
    List<String> findDistinctGenresByMediaType(@Param("mediaType") MediaType mediaType);
}
