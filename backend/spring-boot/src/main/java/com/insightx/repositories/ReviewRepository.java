package com.insightx.repositories;

// ReviewRepository - Data access for user reviews
// Extends JpaRepository for CRUD operations
//
// Custom Query Methods:
// - findByUserId(UUID userId): List<Review>
//   Get all reviews written by user
//
// - findByMediaIdAndMediaType(String mediaId, MediaType mediaType): List<Review>
//   Get all reviews for specific media (future social feature)
//
// - findByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): Optional<Review>
//   Get user's review for specific media
//
// - findByUserIdOrderByCreatedAtDesc(UUID userId): List<Review>
//   Get reviews in reverse chronological order
//
// - existsByUserIdAndMediaIdAndMediaType(UUID userId, String mediaId, MediaType mediaType): boolean
//   Check if user has reviewed media
//
// - findByUserIdAndMediaType(UUID userId, MediaType mediaType): List<Review>
//   Get reviews filtered by media type
//
// - findByMediaIdAndMediaTypeAndIsSpoilerFalse(String mediaId, MediaType mediaType): List<Review>
//   Get non-spoiler reviews for media
//
// - countByUserId(UUID userId): long
//   Count user's total reviews
//
// - countByMediaIdAndMediaType(String mediaId, MediaType mediaType): long
//   Count reviews for specific media
//
// Pagination Support:
// - Page<Review> findByUserId(UUID userId, Pageable pageable)
// - Page<Review> findByMediaIdAndMediaType(String mediaId, MediaType mediaType, Pageable pageable)
//
// Search Queries (Future):
// - @Query for full-text search on reviewText
// - PostgreSQL specific: Use ts_vector for text search
//
// Soft Delete Support:
// - If implementing soft delete, add:
//   findByUserIdAndDeletedFalse(UUID userId): List<Review>
//
// Moderation Queries (Future):
// - findByReportedTrue(): List<Review>
// - findByReportCountGreaterThan(int threshold): List<Review>
//
// Performance Tips:
// - Index on (userId, createdAt)
// - Index on (mediaId, mediaType)
// - Consider GIN index on reviewText for full-text search
// - Limit review text in list views (use projections)