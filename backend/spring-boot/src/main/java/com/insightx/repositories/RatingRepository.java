package com.insightx.repositories;

// RatingRepository - Data access for user ratings
// Extends JpaRepository for CRUD operations
//
// Custom Query Methods:
// - findByUserId(UUID userId): List<Rating>
//   Get all ratings by user (for taste profile generation)
//
// - findByUserIdAndMediaType(UUID userId, MediaType mediaType): List<Rating>
//   Get ratings filtered by media type
//
// - findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): Optional<Rating>
//   Get user's rating for specific media
//
// - findByUserIdOrderByCreatedAtDesc(UUID userId): List<Rating>
//   Get ratings in reverse chronological order
//
// - findByUserIdAndRatingGreaterThanEqual(UUID userId, int minRating): List<Rating>
//   Get highly rated items (rating >= 8) for recommendations
//
// - findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime date): List<Rating>
//   Get recent ratings (for incremental taste profile updates)
//
// - countByUserId(UUID userId): long
//   Count total ratings (determine if profile can be generated)
//
// - findTop10ByUserIdOrderByRatingDesc(UUID userId): List<Rating>
//   Get user's top 10 highest rated items
//
// Aggregation Queries:
// - @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.userId = :userId")
//   Double getAverageRatingByUserId(@Param("userId") UUID userId)
//   Calculate average rating for taste profile
//
// - @Query("SELECT r.mediaType, COUNT(r) FROM Rating r WHERE r.userId = :userId GROUP BY r.mediaType")
//   List<Object[]> getRatingDistributionByMediaType(@Param("userId") UUID userId)
//   Get distribution of ratings across media types
//
// Pagination Support:
// - Page<Rating> findByUserId(UUID userId, Pageable pageable)
//
// Delete Operations:
// - deleteByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): void
//   Remove rating
//
// Performance Considerations:
// - Index on (userId, createdAt) for temporal queries
// - Index on (userId, rating) for high-rated queries
// - Consider materialized views for aggregations