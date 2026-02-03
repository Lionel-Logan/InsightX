package com.insightx.services;

// RatingService - Manages user ratings for media content
//
// Responsibilities:
// - Submit new rating
// - Update existing rating
// - Delete rating
// - Get user's ratings
// - Get rating for specific media
// - Calculate rating statistics
// - Trigger taste profile updates
//
// Key Methods:
//
// submitRating(UUID userId, RatingRequest request): RatingDTO
// - Validate rating value (1-10)
// - Check if rating exists (update vs create)
// - Save rating to database
// - Trigger taste profile recalculation (async)
// - Return rating DTO
//
// updateRating(UUID userId, String mediaId, MediaType mediaType, int newRating): RatingDTO
// - Find existing rating
// - Update rating value
// - Update timestamp
// - Trigger taste profile recalculation
// - Return updated rating
//
// deleteRating(UUID userId, String mediaId, MediaType mediaType): void
// - Find and delete rating
// - Trigger taste profile recalculation
//
// getUserRatings(UUID userId, MediaType mediaType, Pageable pageable): Page<RatingDTO>
// - Get all ratings by user
// - Optional filter by media type
// - Order by date (newest first)
// - Return paginated results
//
// getRatingForMedia(UUID userId, String mediaId, MediaType mediaType): Optional<RatingDTO>
// - Get user's rating for specific media
// - Return Optional.empty() if not rated
//
// getUserRatingStatistics(UUID userId): RatingStatsDTO
// - Total ratings count
// - Average rating
// - Distribution by score (1-10)
// - Distribution by media type
// - Highest rated items
// - Most recent ratings
//
// getHighlyRatedItems(UUID userId, int minRating): List<RatingDTO>
// - Get items rated >= minRating
// - Useful for "favorites" or "highly recommended"
//
// Dependencies:
// - RatingRepository
// - TasteProfileService (for recalculation)
// - WatchedService (optional: auto-mark as watched when rating)
//
// Business Rules:
// - Rating must be 1-10 (validate with @Min, @Max)
// - User can only rate media once (update if exists)
// - Rating implies consumption (optionally auto-mark as watched)
// - Deleting rating doesn't delete watched status
//
// Taste Profile Impact:
// - Trigger recalculation on every 5 new ratings
// - Async recalculation to avoid blocking API
// - Weight recent ratings more than old ones
//
// Transaction Management:
// - @Transactional for write operations
// - Ensure atomic updates
//
// Error Handling:
// - InvalidRatingException (out of range)
// - RatingNotFoundException
// - UserNotFoundException
//
// Performance:
// - Batch rating retrieval for taste profile
// - Cache user's ratings in Redis
// - Invalidate cache on rating changes