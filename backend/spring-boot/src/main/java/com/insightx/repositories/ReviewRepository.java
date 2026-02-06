package com.insightx.repositories;

import com.insightx.entities.MediaType;
import com.insightx.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ReviewRepository - Data access for user reviews
 * 
 * Supports full-text reviews with upvote system and visibility controls.
 * Reviews can be sorted by popularity (upvotes) or recency.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // ========================================
    // Basic Queries
    // ========================================

    /**
     * Get all reviews written by user
     */
    List<Review> findByUserId(UUID userId);

    /**
     * Get all PUBLIC reviews for specific media
     */
    List<Review> findByMediaIdAndMediaTypeAndVisibility(String mediaId, MediaType mediaType, Review.Visibility visibility);

    /**
     * Get user's review for specific media
     */
    Optional<Review> findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    /**
     * Check if user has reviewed media
     */
    boolean existsByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType);

    /**
     * Get reviews filtered by media type
     */
    List<Review> findByUserIdAndMediaType(UUID userId, MediaType mediaType);

    // ========================================
    // Chronological Queries
    // ========================================

    /**
     * Get reviews in reverse chronological order
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Get recent reviews by user
     */
    List<Review> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime date);

    // ========================================
    // Sorted Queries (by upvotes)
    // ========================================

    /**
     * Get reviews for media sorted by upvotes (most liked first)
     */
    Page<Review> findByMediaIdAndMediaTypeAndVisibilityOrderByUpvoteCountDesc(
        String mediaId, 
        MediaType mediaType, 
        Review.Visibility visibility, 
        Pageable pageable
    );

    /**
     * Get reviews for media sorted by recency
     */
    Page<Review> findByMediaIdAndMediaTypeAndVisibilityOrderByCreatedAtDesc(
        String mediaId, 
        MediaType mediaType, 
        Review.Visibility visibility, 
        Pageable pageable
    );

    // ========================================
    // Pagination Support
    // ========================================

    /**
     * Paginated reviews by user
     */
    Page<Review> findByUserId(UUID userId, Pageable pageable);

    /**
     * Paginated reviews by user, ordered by date
     */
    Page<Review> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Paginated reviews for specific media
     */
    Page<Review> findByMediaIdAndMediaType(String mediaId, MediaType mediaType, Pageable pageable);

    // ========================================
    // Aggregation Queries
    // ========================================

    /**
     * Count user's total reviews
     */
    long countByUserId(UUID userId);

    /**
     * Count reviews for specific media
     */
    long countByMediaIdAndMediaType(String mediaId, MediaType mediaType);

    /**
     * Count reviews by user and media type
     */
    long countByUserIdAndMediaType(UUID userId, MediaType mediaType);

    // ========================================
    // Activity Feed Support
    // ========================================

    /**
     * Get recent reviews by multiple users (for activity feed)
     * Only PUBLIC reviews, last 7 days
     */
    @Query("SELECT r FROM Review r WHERE r.userId IN :userIds " +
           "AND r.visibility = 'PUBLIC' " +
           "AND r.createdAt > :since " +
           "ORDER BY r.createdAt DESC")
    List<Review> findRecentByUserIdsPublic(@Param("userIds") List<UUID> userIds, 
                                            @Param("since") LocalDateTime since);

    // ========================================
    // Upvote Management
    // ========================================

    /**
     * Increment upvote count (called when user likes a review)
     */
    @Modifying
    @Query("UPDATE Review r SET r.upvoteCount = r.upvoteCount + 1 WHERE r.id = :reviewId")
    void incrementUpvoteCount(@Param("reviewId") UUID reviewId);

    /**
     * Decrement upvote count (called when user unlikes a review)
     */
    @Modifying
    @Query("UPDATE Review r SET r.upvoteCount = r.upvoteCount - 1 WHERE r.id = :reviewId AND r.upvoteCount > 0")
    void decrementUpvoteCount(@Param("reviewId") UUID reviewId);
}