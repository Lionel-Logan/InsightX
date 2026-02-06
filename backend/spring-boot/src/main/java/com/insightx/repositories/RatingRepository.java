package com.insightx.repositories;

import com.insightx.entities.MediaType;
import com.insightx.entities.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RatingRepository - Data access for user ratings
 * 
 * Central to taste profile generation and personalized recommendations.
 * Supports 1-10 rating scale with visibility controls.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    // ========================================
    // Basic Queries
    // ========================================

    /**
     * Get all ratings by user
     */
    List<Rating> findByUserId(UUID userId);

    /**
     * Get ratings filtered by media type
     */
    List<Rating> findByUserIdAndMediaType(UUID userId, MediaType mediaType);

    /**
     * Get user's rating for specific media
     */
    Optional<Rating> findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    /**
     * Check if user has rated specific media
     */
    boolean existsByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    // ========================================
    // Chronological Queries
    // ========================================

    /**
     * Get ratings in reverse chronological order (for taste profile with recency weighting)
     */
    List<Rating> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Get recent ratings (for incremental taste profile updates)
     */
    List<Rating> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime date);

    // ========================================
    // Filtered Queries
    // ========================================

    /**
     * Get highly rated items (rating >= threshold)
     */
    List<Rating> findByUserIdAndRatingGreaterThanEqual(UUID userId, int minRating);

    /**
     * Get top N highest rated items
     */
    @Query("SELECT r FROM Rating r WHERE r.userId = :userId ORDER BY r.rating DESC, r.createdAt DESC LIMIT :limit")
    List<Rating> findTopRatedByUser(@Param("userId") UUID userId, @Param("limit") int limit);

    // ========================================
    // Pagination Support
    // ========================================

    /**
     * Paginated ratings
     */
    Page<Rating> findByUserId(UUID userId, Pageable pageable);

    /**
     * Paginated ratings by media type
     */
    Page<Rating> findByUserIdAndMediaType(UUID userId, MediaType mediaType, Pageable pageable);

    /**
     * Paginated ratings ordered by date
     */
    Page<Rating> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // ========================================
    // Aggregation Queries
    // ========================================

    /**
     * Count total ratings by user
     */
    long countByUserId(UUID userId);

    /**
     * Count ratings by media type
     */
    long countByUserIdAndMediaType(UUID userId, MediaType mediaType);

    /**
     * Calculate average rating for user
     */
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.userId = :userId")
    Double getAverageRatingByUserId(@Param("userId") UUID userId);

    /**
     * Get rating distribution by media type
     */
    @Query("SELECT r.mediaType, COUNT(r) FROM Rating r WHERE r.userId = :userId GROUP BY r.mediaType")
    List<Object[]> getRatingDistributionByMediaType(@Param("userId") UUID userId);

    /**
     * Get rating distribution by score (1-10)
     */
    @Query("SELECT r.rating, COUNT(r) FROM Rating r WHERE r.userId = :userId GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionByScore(@Param("userId") UUID userId);

    // ========================================
    // Activity Feed Support
    // ========================================

    /**
     * Get recent ratings by multiple users (for activity feed)
     * Only PUBLIC visibility, last 7 days
     */
    @Query("SELECT r FROM Rating r WHERE r.userId IN :userIds " +
           "AND r.visibility = 'PUBLIC' " +
           "AND r.createdAt > :since " +
           "ORDER BY r.createdAt DESC")
    List<Rating> findRecentByUserIdsPublic(@Param("userIds") List<UUID> userIds, 
                                            @Param("since") LocalDateTime since);

    // ========================================
    // Delete Operations
    // ========================================

    /**
     * Remove rating
     */
    void deleteByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);
}