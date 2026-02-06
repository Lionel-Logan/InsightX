package com.insightx.repositories;

import com.insightx.entities.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ReviewLikeRepository - Data access for review likes/upvotes
 * 
 * Manages Instagram-style likes on reviews.
 * Users can like/unlike reviews, with denormalized count in Review entity.
 */
@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

    // ========================================
    // Basic Queries
    // ========================================

    /**
     * Find like by review and user
     */
    Optional<ReviewLike> findByReviewIdAndUserId(UUID reviewId, UUID userId);

    /**
     * Check if user has liked a review
     */
    boolean existsByReviewIdAndUserId(UUID reviewId, UUID userId);

    /**
     * Get all users who liked a review
     */
    List<ReviewLike> findByReviewId(UUID reviewId);

    /**
     * Get all reviews liked by a user
     */
    List<ReviewLike> findByUserId(UUID userId);

    // ========================================
    // Aggregation Queries
    // ========================================

    /**
     * Count total likes for a review
     */
    long countByReviewId(UUID reviewId);

    /**
     * Count total likes by a user (how many reviews they've liked)
     */
    long countByUserId(UUID userId);

    /**
     * Get like counts for multiple reviews (batch operation)
     */
    @Query("SELECT rl.reviewId, COUNT(rl) FROM ReviewLike rl WHERE rl.reviewId IN :reviewIds GROUP BY rl.reviewId")
    List<Object[]> getLikeCountsForReviews(@Param("reviewIds") List<UUID> reviewIds);

    // ========================================
    // Delete Operations
    // ========================================

    /**
     * Remove like (unlike feature)
     */
    void deleteByReviewIdAndUserId(UUID reviewId, UUID userId);

    /**
     * Delete all likes for a review (when review is deleted - handled by CASCADE)
     */
    void deleteByReviewId(UUID reviewId);

    /**
     * Delete all likes by a user (when user is deleted - handled by CASCADE)
     */
    void deleteByUserId(UUID userId);
}
